package coco.bbg;

import coco.name.Name;
import coco.name.Names;
import coco.util.Calendar;
import coco.util.TradingCalendar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class FileTool {

	public static File recent(long pTimestamp, String pDirectory,
			SimpleDateFormat pDateFormat) throws FileNotFoundException {
		File tDir = new File(pDirectory);
		if (!tDir.isDirectory()) {
			return null;
		}
		File[] tFiles = tDir.listFiles();
		List<File> tFilesList = new ArrayList<File>();
		for (File tFile : tFiles) {
			try {
				Date tD = pDateFormat.parse(tFile.getName());
				if (tD.getTime() <= pTimestamp) {
					tFilesList.add(tFile);
				}
			} catch (Exception e) {
			}
		}
		if (tFilesList.size() == 0) {
			return null;
		}
		return Collections.max(tFilesList, new FileSorter(pDateFormat));
	}

	public static List<Name> readBbgTickers(String pFilePath, Names<Name> pCache)
			throws IOException {
		File tFile = new File(pFilePath);
		if (tFile.isDirectory()) {
			return readBbgTickersDir(pFilePath, pCache);
		}
		BufferedReader tReader = new BufferedReader(new FileReader(pFilePath));
		List<String> tAllLines = new ArrayList<String>();
		String tLine;
		while ((tLine = tReader.readLine()) != null) {
			tLine = tLine.trim();
			if (tLine.equals("")) {
				continue;
			}
			if (!tAllLines.contains(tLine)) {
				tAllLines.add(tLine);
			}
		}
		tReader.close();
		List<Name> tNames = new ArrayList<Name>();
		for (String tTicker : tAllLines) {
			StringTokenizer tSt = new StringTokenizer(tTicker, " ");
			String tId = tSt.nextToken();
			Name tName = pCache.queryId(tId);
			if (tName == null) {
				tName = new Name(tId);
				tName.addId(tTicker, Bbg.Constants.BBG);
				pCache.addName(tName);
			}
			tNames.add(tName);
		}
		return tNames;
	}

	public static List<Name> readBbgTickersDir(String pFilePath,
			Names<Name> pCache) throws IOException {
		File tDir = new File(pFilePath);
		if (!tDir.isDirectory()) {
			throw new RuntimeException("not a directory: " + pFilePath);
		}
		File[] tFiles = tDir.listFiles();
		List<String> tAllLines = new ArrayList<String>();
		for (File tFile : tFiles) {
			BufferedReader tReader = new BufferedReader(new FileReader(tFile));
			String tLine;
			while ((tLine = tReader.readLine()) != null) {
				tLine = tLine.trim();
				if (tLine.equals("")) {
					continue;
				}
				if (!tAllLines.contains(tLine)) {
					tAllLines.add(tLine);
				}
			}
			tReader.close();
		}
		List<Name> tNames = new ArrayList<Name>();
		for (String tTicker : tAllLines) {
			StringTokenizer tSt = new StringTokenizer(tTicker, " ");
			String tId = tSt.nextToken();
			Name tName = pCache.queryId(tId);
			if (tName == null) {
				tName = new Name(tId);
				tName.addId(tTicker, Bbg.Constants.BBG);
				pCache.addName(tName);
			}
			tNames.add(tName);
		}
		return tNames;
	}

	public static List<Name> readWithHeader(String pFilePath, Names<Name> pCache)
			throws IOException {
		File tFile = new File(pFilePath);
		if (tFile.isDirectory()) {
			File[] tFiles = tFile.listFiles();
			List<Name> tNames = new ArrayList<Name>();
			for (File tF : tFiles) {
				tNames.addAll(readWithHeaderInternal(tF, pCache));
			}
			return tNames;
		}
		return readWithHeaderInternal(tFile, pCache);
	}

	private static List<Name> readWithHeaderInternal(File tFile,
			Names<Name> pCache) throws IOException {
		if (!tFile.isFile()) {
			throw new RuntimeException("not a file: " + tFile.getAbsolutePath());
		}
		List<Name> tNames = new ArrayList<Name>();
		BufferedReader tReader = new BufferedReader(new FileReader(tFile));
		String[] tHdr = tReader.readLine().split("\t");
		String tLine;
		while ((tLine = tReader.readLine()) != null) {
			tLine = tLine.trim();
			if (tLine.equals("")) {
				continue;
			}
			String[] tIds = tLine.split("\t");
			Name tName = pCache.queryId(tIds[0]);
			if (tName == null) {
				tName = new Name(tIds[0]);
				for (int i = 1; i < tHdr.length; i++) {
					tName.addId(tIds[i], tHdr[i]);
				}
				pCache.addName(tName);
			}
			tNames.add(tName);
		}
		tReader.close();
		return tNames;
	}

	public static List<String> file2list(String pFilePath) {
		try {
			BufferedReader tReader = new BufferedReader(new FileReader(
					pFilePath));
			List<String> tAllLines = new ArrayList<String>();
			String tLine;
			while ((tLine = tReader.readLine()) != null) {
				tLine = tLine.trim();
				if (tLine.equals("")) {
					continue;
				}
				if (!tAllLines.contains(tLine)) {
					tAllLines.add(tLine);
				}
			}
			tReader.close();
			return tAllLines;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}

	public static TradingCalendar readTradingCalendar(String pFilePath)
			throws IOException, ParseException {
		BufferedReader tReader = new BufferedReader(new FileReader(pFilePath));
		List<String> tAllLines = new ArrayList<String>();
		String tLine;
		while ((tLine = tReader.readLine()) != null) {
			tLine = tLine.trim();
			if (tLine.equals("")) {
				continue;
			}
			if (!tAllLines.contains(tLine)) {
				tAllLines.add(tLine);
			}
		}
		tReader.close();
		SimpleDateFormat tSdf = new SimpleDateFormat("yyyy-MM-dd");
		TradingCalendar tTradingCalendar = new TradingCalendar();
		for (String tDateStr : tAllLines) {
			Date tDate = tSdf.parse(tDateStr);
			tTradingCalendar.addHoliday(new Calendar(tDate.getTime(), TimeZone
					.getDefault()));
		}
		return tTradingCalendar;
	}

	private static class FileSorter implements Comparator<File>, Serializable {

		private static final long serialVersionUID = 1L;

		private final SimpleDateFormat mSdf;

		public FileSorter(SimpleDateFormat pDateFormat) {
			mSdf = pDateFormat;
		}

		@Override
		public int compare(File arg0, File arg1) {
			int tRsp = 0;
			Date tD0;
			try {
				tD0 = mSdf.parse(arg0.getName());
			} catch (ParseException e) {
				throw new RuntimeException(e.fillInStackTrace());
			}
			Date tD1;
			try {
				tD1 = mSdf.parse(arg1.getName());
			} catch (ParseException e) {
				throw new RuntimeException(e.fillInStackTrace());
			}
			if (tD0.before(tD1)) {
				tRsp = -1;
			} else if (tD1.before(tD0)) {
				tRsp = 1;
			}
			return tRsp;
		}
	}
}
