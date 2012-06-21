package com.simple.util;
/**
 * IP转换类
 * 来源: http://orgcent.com/ip-address-integer-convert/
 * @author Administrator
 *
 */
public class IpTools {

	public static long ip2int(String ip) {
	    String[] items = ip.split("\\.");
	    return Long.valueOf(items[0]) << 24
	            | Long.valueOf(items[1]) << 16
	            | Long.valueOf(items[2]) << 8 | Long.valueOf(items[3]);
	}

	public static String int2ip(long ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
