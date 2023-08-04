# Changelog
## [1.19.2-1.3.6] 2023-08-03
### Bugfixes / Tweaks
- Fix a crash introduced in 1.3.5
## [1.19.2-1.3.5] 2023-08-03
### Minor Features
- Explosions now also create Reconstruct Blocks outside of raids. The block will reconstruct after killing the mob.
- Added various configurations for the Reconstruct block to reconstruct.
- Introduce an increase to the factionRaidCommand, allowing to increase the target's strength.
### Bugfixes / Tweaks
- Fixed case sensitivity issues with various enumeration fields
- Fixes #41: Small fixes to GAIA faction + make it a config option.
- Fixes #18, making banners have proper names
- Fixes #40: hasRank check on diggers

## [1.19.2-1.3] OVERVIEW
### New Factions
#### End
The End faction has been added. This faction will spawn as soon as you breach the end. The enderman's teleporting behaviour will pose a unique challenge for anyone aggrevating this faction.
#### Piglin
With brain AI available, FactionCraft now supports the Piglin Faction. They will start spawning as soon as you breach the nether. Compatibility planned with Infamous Legends to support the Piglin hordes.
#### Villager
To properly support piglins finding villagers a bad entity to fight, villagers have moved into their own faction. This plays into the faction targetting system and should fix issues with mobs not targetting villagers.
#### Player
Players have now been given their own factions. This change right now only supports some other base code, but it lays the basics for Player controlled factions! 
### Brain AI
There is now facion AI for mobs with a brain, like piglins. This means you can add many more mobs to factions!
### Relationships
A basic relationship system has been implemented. Currently can not be influenced, but the basics are there.
### Minor updates and Bugfixes
A long list of updates and bugfixes has been made. Look below for the full list.

## [1.19.2-1.3.4] - 2023-06-02
### Bugfixes / Tweaks
- Prevent Glare Crash due to AI incompatible with the mod.
- Fix mobs not hitting eachother.
- Added proper LivingChangeTargetEvent, for improved performance.
- Fix #37: Crash due to empty Damage source.
- Fix #38: Horses should no longer do weird stuff. Might also create a minor performance boost.

## [1.19.2-1.3.3] - 2023-05-XX
### Major Features
#### New Faction: End
The End faction has been added. This faction will spawn as soon as you breach the end. The enderma's teleporting behaviour will pose a unique challenge for anyone aggrevating this faction.
### Minor Features
- Implemented a rudimentary Reputation system in factions. Currently without any way to influence it.
- Complete rewrite of all existing factions to include many of the recently added features.
- Added a lot of base code for upcoming changes, preparing worlds for new content coming soon.
### Bugfixes / Tweaks
- Made ReconstructBlocks only collidable when creative and crouching.
- Made Armor and hand boosts support a ResourceSet of applicable entities

## [1.19.2-1.3.2] - 2023-05-05
### Bugfixes / Tweaks
- Fixed crash due to null sound event
- Fixed crashes when there are no avaialble factions
- Changed default for raid config to enabled

## [1.19.2-1.3.1] - 2023-05-01
### Bugfixes / Tweaks
- Fixed crashes with brain mobs that can not attack
- made an adjustment to the check to see if a mob has a brain.

## [1.19.2-1.3.0] - 2023-05-01
### Major Features
#### Brain AI
There is now facion AI for mobs with a brain, like piglins
#### New Faction: piglin
With brain AI available, FactionCraft now supports the Piglin Faction
#### New Faction: villager
To properly support piglins finding villagers a bad entity to fight, villagers have moved into their own faction. This plays into the faction targetting system and should fix issues with all mobs not targetting villagers.
### Minor Features
- There is a new `max_spawned_per_x` field on faction entity types. This field allows the spawned range max to grow dynamically with the size of the wave.
### Bugfixes / Tweaks
- Default entities are now on by default

## [1.19.2-1.2.4] - 2023-XX-XX

### Bugfixes / Tweaks
- Fixed Patrols not spawning

## [1.19.2-1.2.3] - 2023-02-04
### Minor Features

### Bugfixes / Tweaks
- Added missing Default factions
- Fixed Biome Spawning not working properly.

## [1.19.2-1.2.2] - 2023-31-03
### Minor Features
- Moved the wither faction also into the new faction entity type system.
- Faction battles now have more randomized starting waves. Config updated to reflect this. 

### Bugfixes / Tweaks
- Fixed some of the weights and rarities for tank entities.
- Ravagers were pretty cheap to the raid strength.
- Fixes crash when trying to spawn a patrol with no available patrol members.

## [1.19.2-1.2.1] - 2023-31-03

### Major Features
#### Diggers
A new Faction Rank has been added, called `DIGGER`. Mobs with this rank will be summoned when another mob considers itself stuck.
These mobs will be able to dig towards their goal, creating a path for mobs to follow. They will be able to dig through walls, and will be able to dig upwards and downwards.

To achieve this, several a boost type has been created, `digger_boost`, with several boost implementations. Each of these boosts detail how equipped the mob must be to dig. These are:
- `tool_digger`
- `proper_tool_digger`
- `barehanded_digger`

#### Role Boost
The Role Boost Provider has been added. This allows you to add boosts to mobs based on their intend role.

This version provides two roles:
- `potion_healer`: Intended to fix the missing functionality of the witch. The witch will now, once again, throw healing potions at nearby allies.
- `tank` (experimental): Allows mobs to wield and "use" shields. However, due to the nature mob animations, the shield animation will not be visible. This is a limitation of the game, and cannot be fixed by the mod. Instead the shield will be displayed above the head when the mob is shielding. In version 1.20.0, this will become a proper icon. Several other minor issues exist, so it is considered experimental for now and will be disabled by default. Use the new experimental setting in the config to turn it on.

#### Faction Entity Types
Faction Entity Types will, from now on, be in individual files. This will make it easier to add new ones, and to add new features to them. You can also override individual faction entity types to adjust their balancing.  We introduced several minor changes to the way the json file looks, cleaning up various fields into a proper min max object.

The old faction entity typesm in the faction file, are still supported but will be removed in a future version. Please migrate to the new format. New features are not added to these.

#### "FactionSummon" command
A new command has been added to the mod, which allows you to summon a faction entity. The command is as follows:
```
/factionsummon <faction> <entity> <x> <y> <z> <boostStrength>
```

#### Redid Entity Navigation in a village
Entities are now a lot smarter during a raid. We have removed the vanilla raid navigation and improved it to be more intelligent. This means that mobs will now be better at navigating through buildings. Be aware that this will impact the difficulty of raids and no balancing adjustments for this have been made.

#### Default Entities
A faction can now be outfitted with default entities. Whenever an entity spawns, a check will run to see if it has a default faction, if it does, this faction will be added to entity. If multiple factions have it as a default entity, a random one will be selected, seeded by the chunk, to ensure close entities are more likely from the same faction.  

#### Minor Features
- Added Biome Spawning limitations to Faction Entity Types.
- Added Y range check to Faction Entity Types.
- Made the ReconstructBlock also work outside of raids. Right now, they will still be primarily generated only during raids, with the exception of the new diggers.
- Mobs can now VERY rarely spawn with a trident. They will not, however, be able to throw it.
- Ranks are now a list and no longer a min and max. This will allow a mob to appear both as a digger and a soldier, for example.
- Several new tags have been added. These will be use for various new features. Right now, they are only used for the tank role. In the future they will be used for more boosts.
  - `#factioncraft:can_use_shield`
  - `#factioncraft:can_use_crossbow`
  - `#factioncraft:can_use_bow`
  - `#factioncraft:can_use_weapon`
  - `#factioncraft:can_wear_armor`

### Bugfixes / Tweaks
- Fixed raids not ending when babies are still alive.
- Fixed #31, crash during game run.
- Fixed #29, RaidManager being loaded on Client Side.
- Fixed #21, no sound is played for the default illager faction, if it did not in vanilla.
- Fixed #20, Horses buck off their riders.