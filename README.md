# LCLPMMO
A modification for MinecraftForge, to add MMO stuff to Minecraft. Intended to be used on LCLPServer 5.0.

### For players:<br>
Read the [installation instructions](https://lclpnet.work/lclpserver/5.0#installationTitle)

### Using LCLPMMO in your project
Of course, you can also implement LCLPMMO in your project. Just keep in mind that the mod is intended to be used on the official LCLPServer 5.0 Minecraft server.
<br>
To implement it, just add this repositories to your build.gradle:
```groovy
repositories {
    maven { url 'https://repo.repsy.io/mvn/gandiber/geckolib' } // required by LCLPMMO
    maven { url 'https://repo.lclpnet.work/repository/internal' }
}
```
Now add the following dependencies:
```groovy
dependencies {
    implementation fg.deobf("work.lclpnet.mods:CoreBase:VERSION_COREBASE") // required by LCLPMMO
    implementation fg.deobf("work.lclpnet.mods:LCLPMMO:VERSION")
    implementation fg.deobf("software.bernie.geckolib:geckolib-forge-VERSION_MC:VERSION_GECKOLIB") // required by LCLPMMO
}
```
You need to replace `VERSION` with the version you want to use.
To see all versions available, you can [check the repository](https://repo.lclpnet.work/#artifact~internal/work.lclpnet.mods/LCLPMMO).<br>
<br>
Please note that `VERSION_COREBASE` should match the version required of your target LCLPMMO build. To find the correct version, please check the [gradle.properties file](https://github.com/LCLPYT/LCLPMMO/blob/master/gradle.properties), in which the `corebase_version` specifies the required version. Just keep in mind to find the correct commit if you are not using the latest version.<br>
The same thing applies to `VERSION_MC` and `VERSION_GECKOLIB` just check them in the `gradle.properties` of the target LCLPMMO commit.
