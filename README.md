ElectricAddons
==============

A Minecraft Forge mod, addon to IndustrialCraft2, adding some new electrical stuff (machines, power outlets, ...)

Compiling
=========

In order to compile this, you will need a Minecraft Forge environment (with the correct Minecraft version). Simply run
```bash
$ gradle build
```
and add to the .jar obtained all the assets (by the way, I haven't searched a lot on this issue, but if you are aware of a way to make Gradle add the assets to the final .jar, let me know! :))

You should also consider updating the ic2 api (in src/main/java/ic2) to the latest at the time you compile. I leave this code here only to make my code easily buildabe. By the way, I *do not own* the ic2 api code, it belongs to the ic2 dev team exclusively.

Stability
=========

At the moment I only use this mod on my personnal server. I've never encountered any bug, and if I do I'll try to fix it. But if you find one, please report it!

License
=======

This mod is a free mod, released under GNU GPLv3 license, which means you can freely use, modify and redistribute it (in a nutshell). You are also free to use it in a mod pack if you're mad enough to consider it really useful! In this case I would appreciate if you could let me know.
