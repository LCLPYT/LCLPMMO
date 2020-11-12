function initializeCoreMod() {
    return {
        'addSoundChangeEvent': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.audio.SoundHandler',
                'methodName': 'func_184399_a',
                'methodDesc': '(Lnet/minecraft/util/SoundCategory;F)V'
            },
            'transformer': function(method) {
                log("Patching SoundHandler...");
                patch_SoundHandler_setSoundLevel(method);
                return method;
            }
        }
    };
}

var asmapi = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var opcodes = Java.type('org.objectweb.asm.Opcodes');

function patch_SoundHandler_setSoundLevel(method) {
    method.instructions.insert(asmapi.buildMethodCall("work/lclpnet/mmo/util/MMOHooks", "onVolumeChange", "()V", asmapi.MethodType.STATIC));
    log("Successfully patched SoundHandler");
}

function log(s) {
    print("[LCLPMMO] " + s);
}