package io.github.purpleposeidon.oldchests.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions("io.github.purpleposeidon.oldchests.asm")
@IFMLLoadingPlugin.DependsOn("cpw.mods.fml.common.asm.transformers.DeobfuscationTransformer")
public class OldChestLoadingPlugin implements IFMLLoadingPlugin {
    public static boolean deobfuscatedEnvironment = true;
    public static boolean loaded_properly = false;

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
                "io.github.purpleposeidon.oldchests.asm.TransformChests"
        };
    }

    @Override
    public String getModContainerClass() {
        // We use the FMLCorePluginContainsFMLMod manifest attribute
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        deobfuscatedEnvironment = !(Boolean) data.get("runtimeDeobfuscationEnabled");
        loaded_properly = true;
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
