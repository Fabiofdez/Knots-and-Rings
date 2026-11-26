### Additions
- New game rule `knots_and_rings:generate_saplings` defines whether saplings generate near trees during world generation
- New block tags: Sapling shapes are now data-driven and ensure saplings only grow into trees if they are in a valid shape

### Changes
- Sapling drops now data-driven, defined with loot tables
- Sticks dropped from saplings now include compat for the More Stick Variants (MstV) mod
- Sprouts, saplings, and sapling stems now prevent farmland turning to dirt
- More robust block/item registration to more easily implement compatibility for tree types from external mods
- Sapling assets are now always active instead of applied through a resource pack

### Fixes
- Resolve bug with log blocks from external mods sometimes causing crashes
- Fix issues with saplings generating overly crowded and next to other blocks
- Sapling type is now determined by the most common wood block in the tree trunk base instead of leaves blocks, which prevents saplings from generating near trees of the wrong type
- Fix issue where the skylight level was being checked too soon in chunk generation, causing saplings to fail to generate
