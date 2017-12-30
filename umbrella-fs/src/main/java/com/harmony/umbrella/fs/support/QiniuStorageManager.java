package com.harmony.umbrella.fs.support;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.Resource;

import com.harmony.umbrella.fs.StorageManager;
import com.harmony.umbrella.fs.StorageMetadata;
import com.qiniu.util.Auth;

/**
 * @author wuxii@foxmail.com
 */
public class QiniuStorageManager extends AbstractStorageManager implements StorageManager {

    public static final String STORAGE_TYPE = "qiniu";

    Auth auth;

    public QiniuStorageManager() {
        super(STORAGE_TYPE);
    }

    @Override
    public StorageMetadata put(Resource resource, String name, Properties properties) throws IOException {
        throw new UnsupportedOperationException();
        // // 构造一个带指定Zone对象的配置类
        // Configuration cfg = new Configuration(Zone.zone0());
        // // ...其他参数参考类注释
        // UploadManager uploadManager = new UploadManager(cfg);
        // // ...生成上传凭证，然后准备上传
        // String accessKey = "your access key";
        // String secretKey = "your secret key";
        // String bucket = "your bucket name";
        // // 如果是Windows情况下，格式是 D:\\qiniu\\test.png
        // String localFilePath = "/home/qiniu/test.png";
        // // 默认不指定key的情况下，以文件内容的hash值作为文件名
        // String key = null;
        // Auth auth = Auth.create(accessKey, secretKey);
        // String upToken = auth.uploadToken(bucket);
        // try {
        // Response response = uploadManager.put(localFilePath, key, upToken);
        // // 解析上传成功的结果
        // DefaultPutRet putRet = new Gson().fromJson(response.bodyString(),
        // DefaultPutRet.class);
        // System.out.println(putRet.key);
        // System.out.println(putRet.hash);
        // } catch (QiniuException ex) {
        // Response r = ex.response;
        // System.err.println(r.toString());
        // try {
        // System.err.println(r.bodyString());
        // } catch (QiniuException ex2) {
        // // ignore
        // }
        // }
        // return null;
    }

    @Override
    public Resource get(StorageMetadata sm) throws IOException {
        throw new UnsupportedOperationException();
    }

}
