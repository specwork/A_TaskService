package pub.willow.a.taskservice.service.parse.impl;
/**
 * @author paul.ge@timerchina.com
 * @version 创建时间：2013-8-7 下午08:31:04
 * 
 */
public class EditDistance {
	private static String strX;
	private static String strY;
	private static String[] tagArrayX;
	private static String[] tagArrayY;
	
	/**
	 * 计算两个字符串的相似度，用编辑距离计算字符串的相似度
	 * @param strX	字符串x
	 * @param strY	字符串y
	 * @return
	 */
	public static float similarity(String compareStrX, String compareStrY) {
		strX = compareStrX;
		strY = compareStrY;
		int distance = getDistance();
		int length = tagArrayX.length > tagArrayY.length ? tagArrayX.length : tagArrayY.length;
		return 1 - (float)distance/length;
	}
	
	public float similarityByUrl(String compareStrX, String compareStrY) {
		strX = compareStrX;
		strY = compareStrY;
		int distance = getDistanceByUrl();
		int length = tagArrayX.length > tagArrayY.length ? tagArrayX.length : tagArrayY.length;
		return 1 - (float)distance/length;
	}
	
	/**
	 * 获取字符串x和y的编辑距离
	 * @return
	 */
	private static int getDistance() {
		tagArrayX = strX.split(",");
		tagArrayY = strY.split(",");
		return editDistance(tagArrayX.length, tagArrayY.length);
	}
	
	/**
	 * 获取字符串x和y的编辑距离
	 * @return
	 */
	private int getDistanceByUrl() {
		tagArrayX = new String[strX.length()];
		tagArrayY = new String[strY.length()];
		for(int i=0; i<strX.length(); i++) {
			tagArrayX[i] = new String(new char[]{strX.charAt(i)});
		}
		for(int i=0; i<strY.length(); i++) {
			tagArrayY[i] = new String(new char[]{strY.charAt(i)});
		}
		return editDistance(tagArrayX.length, tagArrayY.length);
	}
	
	/**
	 * 计算字符串x和y的编辑距离
	 * @param i
	 * @param j
	 * @return
	 */
	private static int editDistance(int i, int j) {
		int[][] array = new int[i+1][j+1];
		for(int x=0; x<=i; x++)
			array[x][0] = x;
		for(int y=0; y<=j; y++)
			array[0][y] = y;
		
		for(int x=1; x<=i; x++) {
			for(int y=1; y<=j; y++) {
				array[x][y] = minDistance(array[x-1][y]+1, array[x][y-1]+1, array[x-1][y-1]+twoNumDistance(tagArrayX[x-1], tagArrayY[y-1]));
			}
		}
		
		return array[i][j];
	}
	
	/**
	 * 两个字符串是否相等，相等距离为零，不相等距离为1
	 * @param x
	 * @param y
	 * @return
	 */
	private static int twoNumDistance(String x, String y) {
		if(x.equals(y))
			return 0;
		else
			return 1;
	}
	
	/**
	 * 获取最小的距离
	 * @param disa
	 * @param disb
	 * @param disc
	 * @return
	 */
	private static int minDistance(int disa, int disb, int disc) {
		int dismin = Integer.MAX_VALUE;
		if (dismin > disa)
			dismin = disa;
		if (dismin > disb)
			dismin = disb;
		if (dismin > disc)
			dismin = disc;
		return dismin;
	}
	
	public static void main(String[] args) {
		String strX = "A,B,C,D";
		String strY = "A,B,E,C,D";
		float similarity = EditDistance.similarity(strX, strY);
		System.out.println("编辑距离：" + similarity);		
	}

}
