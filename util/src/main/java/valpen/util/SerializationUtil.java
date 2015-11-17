package valpen.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializationUtil {

	private static final String cDirectory = System.getProperty("user.dir")
			+ "/../data/";

	public static void write(Serializable pObject, String pDir, String pId)
			throws IOException {
		if (pObject == null) {
			return;
		}
		File d = new File(cDirectory + pDir);
		if (!d.exists()) {
			d.mkdirs();
		}
		if (!d.isDirectory()) {
			throw new RuntimeException("not a directory: "
					+ d.getAbsolutePath());
		}
		String fn = cDirectory + pDir + "/tmp.ser";
		File f1 = new File(fn);
		f1.delete();
		writeToFile(pObject, fn);
		File f2 = new File(cDirectory + pDir + "/" + pId + ".ser");
		f2.delete();
		f1.renameTo(f2);
	}

	public static Object read(String pDir, String pId) throws IOException,
			ClassNotFoundException {
		String tFileName = cDirectory + pDir + "/" + pId + ".ser";
		Object obj = readFromFile(tFileName);
		return obj;

	}

	private static void writeToFile(Serializable object, String filename)
			throws IOException {
		if (object == null) {
			return;
		}
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(object);
		out.close();
	}

	private static Object readFromFile(String pFilename) throws IOException,
			ClassNotFoundException {
		File file = new File(pFilename);
		if (!file.exists()) {
			return null;
		}
		FileInputStream fis = new FileInputStream(pFilename);
		ObjectInputStream in = new ObjectInputStream(fis);
		Object object = in.readObject();
		in.close();
		return object;
	}
}
