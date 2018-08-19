package com.droidtech.novel.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import com.droidtech.novel.BuildConfig;
import com.droidtech.novel.R;
import com.droidtech.novel.BaseApplication;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * SPEncryptUtils 自定义安全Sharedpreference数据存储类
 * 使用该类可以实现对存储的String字段进行安全加密，避免泄露个人敏感信息
 * 用法示例见： SharedPreferencesUtil方法类
 * 目前app公共模块sharepreference已经实现加密存储
 * add by : wanggang10@jd.com
 */
public final class SPEncryptUtils implements SharedPreferences {

    private static final String TAG = SPEncryptUtils.class.getSimpleName();

    private SharedPreferences mSharedPreferences;

    public SPEncryptUtils(Context context, String name, int mode) {
        mSharedPreferences = context.getSharedPreferences(name, mode);
    }

    @Override
    public Map<String, ?> getAll() {
        return mSharedPreferences.getAll();
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return mSharedPreferences.getStringSet(key, defValues);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return mSharedPreferences.getLong(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return mSharedPreferences.getFloat(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    @Override
    public boolean contains(String key) {
        return mSharedPreferences.contains(key);
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        if (TextUtils.isEmpty(key)) return "";
        String origin = mSharedPreferences.getString(key, "");
        //老数据存在
        if (!TextUtils.isEmpty(origin)) {
            Editor editor = edit();
            //采取加密存取
            editor.putString(key, origin);
            //删除老数据
            editor.remove(key);
            editor.apply();
            return origin;
        } else {
            //重新查找
            String keyEncrypt = String.format("%s_encrypt", key);
            String contentEncrypt = mSharedPreferences.getString(keyEncrypt, "");
            if (TextUtils.isEmpty(contentEncrypt)) {
                return defValue;
            } else {
                String result = Cryptor.decrypt(contentEncrypt);
                return result;
            }
        }
    }

    @Override
    public Editor edit() {
        return new JDEditor(mSharedPreferences.edit());
    }

    //自定义字段写入Editor类
    public class JDEditor implements Editor {

        private Editor mEditor;

        public JDEditor(Editor editor) {
            mEditor = editor;
        }

        @Override
        public Editor putStringSet(String key, @Nullable Set<String> values) {
            return mEditor.putStringSet(key, values);
        }

        @Override
        public Editor putInt(String key, int value) {
            return mEditor.putInt(key, value);
        }

        @Override
        public Editor putLong(String key, long value) {
            return mEditor.putLong(key, value);
        }

        @Override
        public Editor putFloat(String key, float value) {
            return mEditor.putFloat(key, value);
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            return mEditor.putBoolean(key, value);
        }

        @Override
        public Editor remove(String key) {
            return mEditor.remove(key);
        }

        @Override
        public Editor clear() {
            return mEditor.clear();
        }

        @Override
        public boolean commit() {
            return mEditor.commit();
        }

        @Override
        public void apply() {
            mEditor.apply();
        }

        @Override
        public Editor putString(String key, @Nullable String value) {
            if (!TextUtils.isEmpty(key)) {
                //对写入内容进行全部加密
                String enContent = Cryptor.encrypt(value);
                if (!TextUtils.isEmpty(enContent)) {
                    String keyEncrypt = String.format("%s_encrypt", key);
                    mEditor.putString(keyEncrypt, enContent);
                    //删除旧数据
                    mEditor.remove(key);
                }
            }
            return this;
        }
    }

    //采用安全对称加密方式AES128
    public static class Cryptor {
        //私钥片段
        private static final String privateKeyP3 = "JXReeQ==";
        private static final String CHARSET_UTF8 = "UTF-8";
        private static final String CRYPTOR_AES = "AES";

        private static volatile SecretKeySpec sKey;

        //获取对称加密私钥方法
        public interface IPrivateKeyGenerator {
            String getPKP1(); //build.gradle配置

            String getPKP2(); //string资源

            String getPKP3() throws IOException; //Base64反编码字符串

            String getFinalKey() throws IOException; //利用所有结果运算召唤最终私钥
        }

        public static class PrivateKeyGenerator implements IPrivateKeyGenerator {


            @Override
            public String getPKP1() {
                return BuildConfig.privateKeyP1;
            }

            @Override
            public String getPKP2() {
                return BaseApplication.getApplication().getResources().getString(R.string.privateKeyP2);
            }

            @Override
            public String getPKP3() throws IOException {
                return new String(Base64.decode(privateKeyP3, Base64.DEFAULT), CHARSET_UTF8);
            }

            @Override
            public String getFinalKey() throws IOException {
                String p1 = null;
                String p2 = null;
                String p3 = null;
                p1 = getPKP1();
                p2 = getPKP2();
                p3 = getPKP3();
                char[] char1 = p1.toCharArray();
                char[] char2 = p2.toCharArray();
                char[] char3 = p3.toCharArray();
                char[] char4 = new char[char1.length];
                for (int i = 0; i < char1.length; i++) {
                    char4[i] = (char) ((char1[i] + char2[i] + char3[i]) / 3);
                }
                String p4 = String.valueOf(char4);
                StringBuffer buffer = new StringBuffer();
                return buffer.append(p1).append(p2).append(p3).append(p4).toString();
            }

            public static PrivateKeyGenerator newInstance() {
                return new PrivateKeyGenerator();
            }
        }

        private static synchronized SecretKeySpec getAesKey() throws Exception {
            if (sKey == null) {
                KeyGenerator kgen = KeyGenerator.getInstance(CRYPTOR_AES);
                SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
                PrivateKeyGenerator generator = PrivateKeyGenerator.newInstance();
                String privateKey = generator.getFinalKey();

                LogUtils.d(TAG, ">>>>>>>>>>> private key >>>>>>>>>>: " + privateKey);

                sr.setSeed(privateKey.getBytes(CHARSET_UTF8));
                kgen.init(128, sr);
                SecretKey secretKey = kgen.generateKey();
                byte[] enCodeFormat = secretKey.getEncoded();
                sKey = new SecretKeySpec(enCodeFormat, CRYPTOR_AES);
            }
            return sKey;
        }

        public static String encrypt(String value) {
            if (TextUtils.isEmpty(value)) return "";
            try {
                SecretKeySpec key = getAesKey();
                Cipher cipher = Cipher.getInstance(CRYPTOR_AES);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                byte[] bts = value.getBytes(CHARSET_UTF8);
                byte[] result = cipher.doFinal(bts);
                return byte2HexStr(result);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String decrypt(String value) {
            try {
                SecretKeySpec key = getAesKey();
                Cipher cipher = Cipher.getInstance(CRYPTOR_AES);
                cipher.init(Cipher.DECRYPT_MODE, key);
                byte[] bts = hexStr2Byte(value);
                byte[] result = cipher.doFinal(bts);
                return new String(result, CHARSET_UTF8);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        private static String byte2HexStr(byte buf[]) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < buf.length; i++) {
                String hex = Integer.toHexString(buf[i] & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                sb.append(hex.toUpperCase());
            }
            return sb.toString();
        }

        private static byte[] hexStr2Byte(String hexStr) {
            if (hexStr.length() < 1)
                return null;
            byte[] result = new byte[hexStr.length() / 2];
            for (int i = 0; i < hexStr.length() / 2; i++) {
                int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
                int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
                result[i] = (byte) (high * 16 + low);
            }
            return result;
        }
    }
}
