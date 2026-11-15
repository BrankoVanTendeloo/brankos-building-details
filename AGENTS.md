
# Minecraft Mod Development - Agent Instructions

## Project overview
You are helping develop a Minecraft Java Edition mod.

Before making changes, inspect the existing project and understand its architecture.

## Technical requirements
- Use Minecraft version 26.3.
- Use Fabric.
- Use Java version 25.
- Follow the APIs and conventions for the exact Minecraft and mod-loader versions.
- Never assume an API from a newer or older Minecraft version is compatible.

## Coding rules
- Write clean, maintainable, readable Java code.
- Keep classes focused on a single responsibility.
- Avoid unnecessary dependencies.
- Follow existing naming conventions.
- Avoid duplicating code.
- Keep client-side and server-side logic separate.
- Make multiplayer functionality server-authoritative.
- Consider performance, especially during world ticks.

## Minecraft-specific rules
- Register blocks, items, entities, and other content properly.
- Include all required models, textures references, blockstates, loot tables, recipes, and language files.
- Use correct resource locations and namespaces.
- Ensure block and item identifiers are consistent.
- Avoid hardcoding values that should be configurable.
- Consider compatibility with dedicated servers.

## Development workflow
Before implementing a feature:
1. Inspect the relevant existing code.
2. Identify the files that need changing.
3. Explain the intended implementation briefly.
4. Implement the feature.
5. Build the project and resolve compilation errors.
6. Check for missing resources and registrations.
7. Summarize the changes.

## Important restrictions
- Do not replace existing systems without a good reason.
- Do not delete working functionality to make a feature easier.
- Do not invent Minecraft APIs or methods.
- Consult the appropriate version-specific documentation when uncertain.
- Do not silently change the Minecraft version or mod loader.
- Ask before introducing major architectural changes.
- Do not do more than asked.

## Testing
- Run the Gradle build after meaningful changes.
- Never claim something works without verifying it.

## Communication
- Explain important design decisions.
- Identify files created or modified.
- Mention potential bugs or compatibility concerns.
- Ask questions when requirements are ambiguous.
