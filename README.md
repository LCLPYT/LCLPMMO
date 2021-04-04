# LCLPMMO
A modification for MinecraftForge, to add MMO stuff to Minecraft. Intended to be used on LCLPServer 5.0.

### For players:<br>
Read the [installation instructions](https://lclpnet.work/lclpserver/5.0#installationTitle)

### Using LCLPMMO in your project
Of course, you can also implement LCLPMMO in your project. Just keep in mind that the mod is intended to be used on the official LCLPServer 5.0 Minecraft server.
<br>
To implement it, just add this repository to your build.gradle:
```groovy
repositories {
    maven { url 'https://repo.lclpnet.work/repository/internal' }
}
```
Now add the following dependency:
```groovy
dependencies {
    implementation fg.deobf("work.lclpnet.mods:LCLPMMO:VERSION")
}
```
You need to replace VERSION with the version you want to use.
To see all versions available, you can [check the repository](https://repo.lclpnet.work/#artifact~internal/work.lclpnet.mods/LCLPMMO).
