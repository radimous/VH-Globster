package com.radimous.globster.mixin;

import iskallia.vault.util.GlobUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Mixin(value = GlobUtils.class, remap = false)
public class MixinGlobUtils {

    private static Map<String, Pattern> glob2pattern = new HashMap<>();

    @Inject(method = "matches", at = @At("HEAD"), cancellable = true)
    private static void cachedMatches(String glob, String text, CallbackInfoReturnable<Boolean> cir){
        Pattern pattern = glob2pattern.get(glob);
        if (pattern != null) {
            cir.setReturnValue(pattern.matcher(text).matches());
        }
    }

    @Inject(method = "matches", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void cachePattern(String glob, String text, CallbackInfoReturnable<Boolean> cir, Pattern pattern){
        glob2pattern.put(glob, pattern);
    }
}
