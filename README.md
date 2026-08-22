# Object Marker

Minimal RuneLite development plugin for live visual markers.

## v0.3

Adds a RuneLite sidebar for live markers.

Supported marker types:

- Scene objects, including game, wall, decorative and ground objects
- Ground items
- NPCs
- Other players

Each sidebar marker has:

- Match name (`*` can be used for all entries of that type)
- Optional custom label
- Custom colour
- Fill opacity from 0% to 100%
- Individual enabled toggle
- Delete action

Changes are applied live while RuneLite is running and are persisted through RuneLite configuration.

The existing `Mark other players` plugin setting remains available as a quick global player highlight toggle.

## Local test

Use Java 11 and run the Gradle `run` task. The development RuneLite client loads `ObjectMarkerPlugin` automatically.

## Current scope

The plugin only visualises entities. Right-click object picking, import/export profiles, minimap recolouring and automation are intentionally not part of v0.3.
