package pl.rembol.jme3.rts;

import com.jme3.asset.*;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Caps;
import com.jme3.scene.Spatial;
import com.jme3.shader.Shader;
import com.jme3.shader.ShaderGenerator;
import com.jme3.shader.ShaderKey;
import com.jme3.texture.Texture;

import java.util.EnumSet;
import java.util.List;

public class AssetManagerWrapper implements AssetManager {

    private final AssetManager delegate;

    public AssetManagerWrapper(AssetManager delegate) {
        this.delegate = delegate;
    }

    @Override
    public void addClassLoader(ClassLoader classLoader) {
        delegate.addClassLoader(classLoader);
    }

    @Override
    public void removeClassLoader(ClassLoader classLoader) {
        delegate.removeClassLoader(classLoader);
    }

    @Override
    public List<ClassLoader> getClassLoaders() {
        return delegate.getClassLoaders();
    }

    @Override
    public void registerLoader(String s, String... strings) {
        delegate.registerLoader(s, strings);
    }

    @Override
    public void registerLocator(String s, String s1) {
        delegate.registerLocator(s, s1);
    }

    @Override
    public void registerLoader(Class<? extends AssetLoader> aClass, String... strings) {
        delegate.registerLoader(aClass, strings);
    }

    @Override
    public void unregisterLoader(Class<? extends AssetLoader> aClass) {
        delegate.unregisterLoader(aClass);
    }

    @Override
    public void registerLocator(String s, Class<? extends AssetLocator> aClass) {
        delegate.registerLocator(s, aClass);
    }

    @Override
    public void unregisterLocator(String s, Class<? extends AssetLocator> aClass) {
        delegate.unregisterLocator(s, aClass);
    }

    @Override
    public void addAssetEventListener(AssetEventListener assetEventListener) {
        delegate.addAssetEventListener(assetEventListener);
    }

    @Override
    public void removeAssetEventListener(AssetEventListener assetEventListener) {
        delegate.removeAssetEventListener(assetEventListener);
    }

    @Override
    public void clearAssetEventListeners() {
        delegate.clearAssetEventListeners();
    }

    @Override
    public void setAssetEventListener(AssetEventListener assetEventListener) {
        delegate.setAssetEventListener(assetEventListener);
    }

    @Override
    public AssetInfo locateAsset(AssetKey<?> assetKey) {
        return delegate.locateAsset(assetKey);
    }

    @Override
    public <T> T loadAsset(AssetKey<T> assetKey) {
        return delegate.loadAsset(assetKey);
    }

    @Override
    public Object loadAsset(String s) {
        return delegate.loadAsset(s);
    }

    @Override
    public Texture loadTexture(TextureKey textureKey) {
        return delegate.loadTexture(textureKey);
    }

    @Override
    public Texture loadTexture(String s) {
        return delegate.loadTexture(s);
    }

    @Override
    public AudioData loadAudio(AudioKey audioKey) {
        return delegate.loadAudio(audioKey);
    }

    @Override
    public AudioData loadAudio(String s) {
        return delegate.loadAudio(s);
    }

    @Override
    public Spatial loadModel(ModelKey modelKey) {
        return ModelHelper.rewriteDiffuseToAmbient(delegate.loadModel(modelKey));
    }

    @Override
    public Spatial loadModel(String s) {
        return ModelHelper.rewriteDiffuseToAmbient(delegate.loadModel(s));
    }

    @Override
    public Material loadMaterial(String s) {
        return delegate.loadMaterial(s);
    }

    @Override
    public Shader loadShader(ShaderKey shaderKey) {
        return delegate.loadShader(shaderKey);
    }

    @Override
    public BitmapFont loadFont(String s) {
        return delegate.loadFont(s);
    }

    @Override
    public FilterPostProcessor loadFilter(FilterKey filterKey) {
        return delegate.loadFilter(filterKey);
    }

    @Override
    public FilterPostProcessor loadFilter(String s) {
        return delegate.loadFilter(s);
    }

    @Override
    public void setShaderGenerator(ShaderGenerator shaderGenerator) {
        delegate.setShaderGenerator(shaderGenerator);
    }

    @Override
    public ShaderGenerator getShaderGenerator(EnumSet<Caps> enumSet) {
        return delegate.getShaderGenerator(enumSet);
    }
}
