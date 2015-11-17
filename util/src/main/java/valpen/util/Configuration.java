package valpen.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Configuration {

	public static double[] parseDoubleArray(String pString) {
		if (!pString.startsWith("{") || !pString.endsWith("}")) {
			throw new RuntimeException();
		}
		pString = pString.substring(1);
		pString = pString.substring(0, pString.length());
		String[] s = pString.split(",");
		double[] d = new double[s.length];
		for (int i = 0; i < s.length; i++) {
			d[i] = Double.parseDouble(s[i]);
		}
		return d;
	}

	public static int[] parseIntArray(String pString) {
		if (!pString.startsWith("{") || !pString.endsWith("}")) {
			throw new RuntimeException();
		}
		pString = pString.substring(1);
		pString = pString.substring(0, pString.length());
		String[] s = pString.split(",");
		int[] d = new int[s.length];
		for (int i = 0; i < s.length; i++) {
			d[i] = Integer.parseInt(s[i]);
		}
		return d;
	}

	public static boolean[] parseBoolArray(String pString) {
		if (!pString.startsWith("{") || !pString.endsWith("}")) {
			throw new RuntimeException();
		}
		pString = pString.substring(1);
		pString = pString.substring(0, pString.length());
		String[] s = pString.split(",");
		boolean[] d = new boolean[s.length];
		for (int i = 0; i < s.length; i++) {
			if (!s[i].equalsIgnoreCase("true")
					&& !s[i].equalsIgnoreCase("false")) {
				throw new RuntimeException();
			}
			d[i] = Boolean.parseBoolean(s[i]);
		}
		return d;
	}

	private final Map<String, Object> mParamMap = new HashMap<String, Object>();

	public void clear() {
		mParamMap.clear();
	}

	public void read(String pFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(pFile));
		String line;
		while ((line = reader.readLine()) != null) {
			if (!line.trim().equals("") && !line.startsWith("#")) {
				String[] params = line.split("=", 2);
				String parameterName = params[0].trim();
				String parameterValue = params[1].trim();
				boolean success = false;
				if (!success) {
					try {
						if (!parameterValue.equalsIgnoreCase("true")
								&& !parameterValue.equalsIgnoreCase("false")) {
							throw new Exception();
						}
						Boolean value = Boolean.parseBoolean(parameterValue);
						mParamMap.put(parameterName, value);
						success = true;
					} catch (Exception e) {
					}
				}
				if (!success) {
					try {
						Integer value = Integer.parseInt(parameterValue);
						mParamMap.put(parameterName, value);
						success = true;
					} catch (Exception e) {
					}
				}
				if (!success) {
					try {
						Double value = Double.parseDouble(parameterValue);
						mParamMap.put(parameterName, value);
						success = true;
					} catch (Exception e) {
					}
				}
				if (!success) {
					try {
						int[] value = parseIntArray(parameterValue);
						mParamMap.put(parameterName, value);
						success = true;
					} catch (Exception e) {
					}
				}
				if (!success) {
					try {
						double[] value = parseDoubleArray(parameterValue);
						mParamMap.put(parameterName, value);
						success = true;
					} catch (Exception e) {
					}
				}
				if (!success) {
					try {
						boolean[] value = parseBoolArray(parameterValue);
						mParamMap.put(parameterName, value);
						success = true;
					} catch (Exception e) {
					}
				}
				if (!success) {
					mParamMap.put(parameterName, parameterValue);
				}
			}
		}
		reader.close();
	}

	public Boolean getBoolean(String pParameterName) {
		return (Boolean) mParamMap.get(pParameterName);
	}

	public Boolean getBoolean(String pParameterName, boolean pDefaultValue) {
		Boolean b = (Boolean) mParamMap.get(pParameterName);
		return b == null ? pDefaultValue : b;
	}

	public String getString(String pParameterName) {
		return (String) mParamMap.get(pParameterName);
	}

	public String getString(String pParameterValue, String pDefaultValue) {
		String d = getString(pParameterValue);
		return d == null ? pDefaultValue : d;
	}

	public Double getDouble(String pParameterValue, Double pDefaultValue) {
		Double d = getDouble(pParameterValue);
		return d == null ? pDefaultValue : d;
	}

	public Double getDouble(String pParameterName) {
		Object o = mParamMap.get(pParameterName);
		if (o instanceof Integer) {
			return ((Integer) o).doubleValue();
		}
		return (Double) mParamMap.get(pParameterName);
	}

	public int[] getIntArray(String pParameterName, int[] pDefaultValue) {
		Object o = mParamMap.get(pParameterName);
		if (o instanceof Integer[]) {
			return (int[]) o;
		}
		return pDefaultValue;
	}

	public int[] getIntArray(String pParameterName) {
		int[] arr = getIntArray(pParameterName, null);
		if (arr == null) {
			throw new RuntimeException("no parameter value: " + pParameterName);
		}
		return arr;
	}

	public double[] getDoubleArray(String pParameterName, double[] pDefaultValue) {
		Object o = mParamMap.get(pParameterName);
		if (o instanceof Double[]) {
			return (double[]) o;
		}
		return pDefaultValue;
	}

	public double[] getDoubleArray(String pParameterName) {
		double[] arr = getDoubleArray(pParameterName, null);
		if (arr == null) {
			throw new RuntimeException("no parameter value: " + pParameterName);
		}
		return arr;
	}

	public Integer getInteger(String pParameterName, Integer pDefaultValue) {
		Object o = mParamMap.get(pParameterName);
		if (o instanceof Integer) {
			return (Integer) o;
		}
		return pDefaultValue;
	}

	public Integer getInteger(String pParameterName) {
		Integer i = getInteger(pParameterName, null);
		if (i == null) {
			throw new RuntimeException("no parameter value: " + pParameterName);
		}
		return i;
	}

	public void put(String pKey, Object pValue) {
		mParamMap.put(pKey, pValue);
	}
}
