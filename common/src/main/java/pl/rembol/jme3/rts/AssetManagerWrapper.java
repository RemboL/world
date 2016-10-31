package pl.rembol.jme3.rts;

import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.AssetManager;
import com.jme3.asset.FilterKey;
import com.jme3.asset.ModelKey;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Caps;
import com.jme3.scene.Spatial;
import com.jme3.shader.ShaderGenerator;
import com.jme3.texture.Texture;

import java.io.InputStream;
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
    public AssetInfo locateAsset(AssetKey<?> assetKey) {
        return delegate.locateAsset(assetKey);
    }

    @Override
    public <T> T loadAssetFromStream(AssetKey<T> key, InputStream inputStream) {
        return null;
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

    @Override
    public <T> T getFromCache(AssetKey<T> key) {
        return delegate.getFromCache(key);
    }

    @Override
    public <T> void addToCache(AssetKey<T> key, T asset) {
        delegate.addToCache(key, asset);
    }

    @Override
    public <T> boolean deleteFromCache(AssetKey<T> key) {
        return delegate.deleteFromCache(key);
    }

    @Override
    public void clearCache() {
        delegate.clearCache();
    }
}
