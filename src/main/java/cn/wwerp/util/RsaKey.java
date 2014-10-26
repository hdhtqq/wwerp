package cn.wwerp.util;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import java.io.*;
import java.security.spec.*;

public class RsaKey {
	/**
	 * <p>
	 * 生成密钥对(公钥和私钥)
	 * </p>
	 * 
	 * @throws Exception
	 */
	public static void genKeyPair() throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(512);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		// 保存公钥的字节数组
		File f = new File("publicKey.dat"); // 保存公钥到文件publicKey.dat
		FileOutputStream fout = new FileOutputStream(f);
		fout.write(publicKey.getEncoded()); // 得到公钥的字节数组
		fout.close(); // 关闭文件输出流
		// 保存私钥到字节数组
		File f1 = new File("privateKey.dat"); // 保存私钥到文件privateKey.dat
		FileOutputStream fout1 = new FileOutputStream(f1);
		fout1.write(privateKey.getEncoded()); // 得到私钥的字节数组
		fout1.close(); // 关闭文件输出流
	}

	/**
	 * <p>
	 * 从文件中获取公钥并对公钥进行解码
	 * </p>
	 * 
	 * @throws Exception
	 */
	public static void getPublicKey() throws Exception {

		FileInputStream fin = new FileInputStream("publicKey.dat");

		ByteArrayOutputStream baout = new ByteArrayOutputStream();
		byte[] b = new byte[512];
		int aByte = 0;
		while ((aByte = fin.read(b)) != -1) {
			baout.write(b);
		}
		fin.close();
		byte[] keyBytes = baout.toByteArray();
		baout.close();
		// 1:从字节数组解码公钥

		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
		// 将公钥和模进行Base64编码

		RSAPublicKeySpec publicSpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
		BigInteger modulus = publicSpec.getModulus();
		BigInteger exponent = publicSpec.getPublicExponent();
		byte[] ary_m = modulus.toByteArray();

		byte[] ary_e = exponent.toByteArray();
		String str_m;
		String str_e;
		if (ary_m[0] == 0 && ary_m.length == 65)
		// 判断数组首元素是否为0，若是，则将其删除，保证模的位数是128
		{
			byte[] temp = new byte[ary_m.length - 1];
			for (int i = 1; i < ary_m.length; i++) {
				temp[i - 1] = ary_m[i];
			}
			str_m = new String(Base64.encodeBase64(temp));
		} else {

			str_m = new String(Base64.encodeBase64(ary_m));
		}
		str_e = new String(Base64.encodeBase64(ary_e));
		System.out.println("公钥为：" + str_e);
		System.out.println("模为：" + str_m);
	}

	/**
	 * <p>
	 * 获取私钥
	 * </p>
	 * 
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey() throws Exception {
		// 2:从文件中得到私钥编码的字节数组

		FileInputStream fin1 = new FileInputStream("C:/yxon/tmp/privateKey.dat"); // 打开privateKey.dat
		ByteArrayOutputStream baout1 = new ByteArrayOutputStream(); // 用于写入文件的字节流
		byte[] b1 = new byte[1536];
		int aByte1 = 0;
		while ((aByte1 = fin1.read(b1)) != -1) // 从文件读取一个字节
		{
			baout1.write(b1); // 写入一个字节
		}
		fin1.close(); // 关闭文件输入流
		byte[] keyBytes1 = baout1.toByteArray(); // 得到私钥的字节数组
		baout1.close(); // 关闭字节数组输出流
		// 2:从字节数组解码私钥

		PKCS8EncodedKeySpec keySpec1 = new PKCS8EncodedKeySpec(keyBytes1);
		KeyFactory keyFactory1 = KeyFactory.getInstance("RSA"); // 指定算法RSA,得到一个KeyFactory的实例
		PrivateKey privateKey = keyFactory1.generatePrivate(keySpec1);

		return privateKey;
	}

	public static void main(String[] args) throws Exception {
		// RsaKey.genKeyPair();//生成公私钥对并保存到相应文件里面

		PrivateKey privateKey = RsaKey.getPrivateKey(); // 获取私钥

		String str_en = "965edaad64117284800cb15d2a55ecefa7a5adadd4488fb272a98bd5677f1d8488db0ab15a3798672c3b243ccf8a912903a157a030e7de17de1c810e52e1d034"; // 把C#加密的密文输入到str_en字符串

//		byte[] ary_en = Base64.decodeBase64(str_en.getBytes());
		byte[] ary_en = Util.decodeHex(str_en.toCharArray());
		// 解密（
		// 注意Cipher初始化时的参数“RSA/ECB/PKCS1Padding”,代表和.NET用相同的填充算法，如果是标准RSA加密，则参数为“RSA”）
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] deBytes = cipher.doFinal(ary_en);
		String s = new String(deBytes);
		System.out.println("解密结果为：" + s);

	}
}
