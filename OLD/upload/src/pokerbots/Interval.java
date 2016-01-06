package pokerbots;

class Interval {
	public double lower;
	public double upper;
	
	public Interval(double l, double u) {
		lower = l;
		upper = u;
	}
	
	public String toString() {
		String out = "[";
		out+=lower;
		out+=",";
		out+=upper;
		out+="]";
		return out;
	}
}