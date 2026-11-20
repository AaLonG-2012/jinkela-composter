package io.github.aalong2012.jinkela;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;

public final class ModSounds {
	/**
	 * A single sound event with several variants: {@code assets/jinkela-composter/sounds.json} lists
	 * nine clips under the same name, so the client picks one at random every time it is played.
	 */
	public static final ResourceKey<SoundEvent> JINKELA_KEY =
		ResourceKey.create(Registries.SOUND_EVENT, JinKelaComposter.id("jinkela"));

	public static final SoundEvent JINKELA = SoundEvent.createVariableRangeEvent(JinKelaComposter.id("jinkela"));

	public static void register() {
		Registry.register(BuiltInRegistries.SOUND_EVENT, JINKELA_KEY, JINKELA);
	}

	private ModSounds() {
	}
}
