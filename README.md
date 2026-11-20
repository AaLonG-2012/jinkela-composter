# 生产金克拉 / JinKela Composter

[![build](https://github.com/AaLonG-2012/jinkela-composter/actions/workflows/build.yml/badge.svg)](https://github.com/AaLonG-2012/jinkela-composter/actions/workflows/build.yml)
[![release](https://img.shields.io/github/v/release/AaLonG-2012/jinkela-composter)](https://github.com/AaLonG-2012/jinkela-composter/releases/latest)
[![license](https://img.shields.io/github/license/AaLonG-2012/jinkela-composter)](LICENSE)

站在堆肥桶上方潜行，赌一发金坷垃。

Sneak on top of a composter and roll for JinKela.

## 下载 / Download

[**最新版本**](https://github.com/AaLonG-2012/jinkela-composter/releases/latest) · `jinkela-composter-1.1.0.jar`

| 需要 | 版本 |
|---|---|
| Minecraft | 26.3 |
| Fabric Loader | 0.20.1+ |
| [Fabric API](https://modrinth.com/mod/fabric-api) | 0.166.2+26.3 |

把 jar 丢进 `mods/` 即可，客户端与服务端都能装（判定全在服务端跑）。

## 玩法 / Gameplay

站在**堆肥桶上方**（脚踩在桶顶那一层）按一次潜行键 = 一次抽奖判定：

1. **90%** 概率触发这次抽奖；剩下 10% 完全没反应（没有声音，也不填充）。
2. 抽奖一旦触发，**100%** 播放「金坷垃」音效：从 9 个音源里随机挑一个，音调在 **0.85–1.20** 之间随机。
3. 同一次抽奖另有 **85%** 概率把这个堆肥桶**填充一层**（和投入一个可堆肥物一样，7 层之后 1 秒变「可用」，右键取骨粉）。

细节：

- 只认**按下**的那一瞬间（潜行键从松开到按下），按住 shift 一直蹲着**不会**重复触发。
- 两次抽奖之间最少隔 6 tick（0.3 秒），连点器不会刷出一片声音。
- 站在桶里（踩在桶底）不算，必须是**站在桶上方**。
- 创造模式飞行、旁观者、死亡状态不触发。

> **站位提示**：原版堆肥桶是个"杯子"——四周 2px 壁、中间 12×12 px（0.75 格）空腔，所以站在正中央会直接掉进桶里。正确姿势是**贴着桶边站、踩在桶沿上**；放两个桶并排站接缝上最好站。

Sneak **while standing on top of** a composter to roll once:

- 90% - the draw happens; the other 10% is completely silent.
- The draw always plays a JinKela sound: one of nine clips picked at random, with a random pitch between 0.85 and 1.20.
- 85% - that composter gains one layer (identical to inserting a compostable item).

Only the rising edge of the sneak key counts, so holding shift does nothing. A 6-tick cooldown keeps auto-clickers from flooding the area.

## 数值 / Numbers

| 参数 | 值 | 说明 |
|---|---|---|
| 触发概率 | 0.90 | 每次潜行判定 |
| 填充概率 | 0.85 | 触发抽奖之后独立再掷 |
| 实际填充率 | 0.77 | 0.90 × 0.85 |
| 冷却 | 6 tick | 两次抽奖最小间隔 |
| 音调范围 | 0.85–1.20 | 每次播放随机 |
| 音源 | 9 个 | `assets/jinkela-composter/sounds/jinkela_*.ogg` |

参数都在 [`SneakDrawHandler.java`](src/main/java/io/github/aalong2012/jinkela/SneakDrawHandler.java) 顶部，改常量即可。

## 构建 / Build

```bash
./gradlew build        # 产物 build/libs/
./gradlew runClient    # 开发客户端
./gradlew runServer    # 开发服务端（需在 run/eula.txt 写 eula=true）
```

需要 JDK 25。CI 会在每次 push 时用 JDK 25 构建并上传产物。

## 音效 / Sounds

原始素材是 9 个 MP3，已统一转换成 Minecraft 能吃的格式：**44.1 kHz 单声道 OGG Vorbis**，并做过响度统一（目标 -13 LUFS，真峰值 ≤ -1 dB），所以 9 个音源切换时音量不会忽大忽小。

Minecraft only plays OGG, and positional audio needs mono — hence the conversion.

## 许可 / License

[CC0-1.0](LICENSE)
