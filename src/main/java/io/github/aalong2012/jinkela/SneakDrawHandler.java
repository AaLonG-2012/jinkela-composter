package io.github.aalong2012.jinkela;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * One sneak press on top of a composter runs one draw.
 *
 * <p>Roll order:
 * <ol>
 *   <li>90% - the draw happens at all. The other 10% stays completely silent.</li>
 *   <li>100% (once step 1 passed) - play the JinKela sound with a random clip and a random pitch.</li>
 *   <li>60% - fill that composter by exactly one layer.</li>
 * </ol>
 *
 * <p>Only the rising edge of the sneak key counts, so holding shift does nothing; a short cooldown
 * keeps auto-clickers from firing several draws per second.
 */
public final class SneakDrawHandler {
	/** Chance that a sneak press on a composter triggers a draw. */
	private static final float TRIGGER_CHANCE = 0.90F;
	/** Chance that a triggered draw actually fills the composter by one layer. */
	private static final float FILL_CHANCE = 0.60F;
	/** Minimum ticks between two draws, so mashing shift cannot flood the area with sound. */
	private static final int COOLDOWN_TICKS = 6;
	private static final float PITCH_MIN = 0.85F;
	private static final float PITCH_MAX = 1.20F;
	private static final float VOLUME = 1.0F;

	/** How far the feet may sit above the composter's block origin to count as "standing on top". */
	private static final double MIN_FEET_OFFSET = 0.9D;
	private static final double MAX_FEET_OFFSET = 1.1D;

	private static final Map<UUID, SneakState> STATES = new HashMap<>();

	private static final class SneakState {
		private boolean sneaking;
		private long cooldownUntil;
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(SneakDrawHandler::onEndTick);
	}

	private static void onEndTick(MinecraftServer server) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			SneakState state = STATES.computeIfAbsent(player.getUUID(), key -> new SneakState());

			boolean sneaking = player.isShiftKeyDown();
			boolean pressed = sneaking && !state.sneaking;
			state.sneaking = sneaking;

			if (pressed) {
				tryDraw(player, state);
			}
		}

		STATES.keySet().removeIf(uuid -> server.getPlayerList().getPlayer(uuid) == null);
	}

	private static void tryDraw(ServerPlayer player, SneakState state) {
		ServerLevel level = player.level();
		long now = level.getGameTime();

		if (now < state.cooldownUntil) {
			return;
		}

		state.cooldownUntil = now + COOLDOWN_TICKS;

		if (player.isSpectator() || !player.isAlive() || !player.onGround()) {
			return;
		}

		BlockPos pos = composterUnderFeet(player);

		if (pos == null) {
			return;
		}

		RandomSource random = level.getRandom();

		if (random.nextFloat() >= TRIGGER_CHANCE) {
			// The 10% that does not even reach the draw: no sound, no fill.
			return;
		}

		float pitch = PITCH_MIN + random.nextFloat() * (PITCH_MAX - PITCH_MIN);
		level.playSound(null, pos, ModSounds.JINKELA, SoundSource.BLOCKS, VOLUME, pitch);

		if (random.nextFloat() < FILL_CHANCE) {
			fillOnce(level, pos);
		}
	}

	/**
	 * Adds exactly one compost layer, mirroring what vanilla does when a compostable item goes in:
	 * the level stops at {@link ComposterBlock#MAX_LEVEL} and a scheduled tick turns 7 into the
	 * ready state (8) one second later.
	 */
	private static void fillOnce(ServerLevel level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);

		if (!state.is(Blocks.COMPOSTER)) {
			return;
		}

		int current = state.getValue(ComposterBlock.LEVEL);

		if (current >= ComposterBlock.READY) {
			// Already full and ready to be harvested, nothing to add.
			return;
		}

		BlockState filled = state.setValue(ComposterBlock.LEVEL, Math.min(current + 1, ComposterBlock.MAX_LEVEL));

		if (filled == state) {
			return;
		}

		level.setBlockAndUpdate(pos, filled);

		if (filled.getValue(ComposterBlock.LEVEL) >= ComposterBlock.MAX_LEVEL) {
			level.scheduleTick(pos, filled.getBlock(), 20);
		}

		// The vanilla "an item went in" particles, so the player can see the fill.
		level.levelEvent(1500, pos, 1);
	}

	/**
	 * Returns the composter the player is standing on, or {@code null}.
	 *
	 * <p>The vanilla composter is a cup: two pixels of wall and floor around a 12x12 pixel hole, so
	 * standing "on" one usually means standing on its rim, with the hitbox hanging over the block
	 * edge. That is why every block under the player's footprint is checked instead of only the one
	 * below {@code player.blockPosition()}.
	 */
	private static BlockPos composterUnderFeet(ServerPlayer player) {
		AABB box = player.getBoundingBox();
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		int blockY = Mth.floor(box.minY - 0.05D);

		for (int x = Mth.floor(box.minX); x <= Mth.floor(box.maxX); x++) {
			for (int z = Mth.floor(box.minZ); z <= Mth.floor(box.maxZ); z++) {
				cursor.set(x, blockY, z);

				if (!player.level().getBlockState(cursor).is(Blocks.COMPOSTER)) {
					continue;
				}

				double feetOffset = box.minY - cursor.getY();

				if (feetOffset >= MIN_FEET_OFFSET && feetOffset <= MAX_FEET_OFFSET) {
					return cursor.immutable();
				}
			}
		}

		return null;
	}

	private SneakDrawHandler() {
	}
}
