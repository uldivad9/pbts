package pokerbots;

import java.util.ArrayList;

class SortedList {
	ArrayList<Double> list;
	
	public SortedList() {
		list = new ArrayList<Double>();
	}
	
	public void insert(double d) {
		if (list.size() == 0) {
			list.add(d);
			return;
		} else if (d <= list.get(0)) {
			list.add(0,d);
			return;
		} else if (d >= list.get(list.size()-1)) {
			list.add(d);
			return;
		}
		
		int lo = 1;
		int hi = list.size()-1;
		while (lo != hi) {
			int mid = (lo+hi)/2;
			if (list.get(mid) < d) {
				lo = mid+1;
			} else if (list.get(mid-1) > d) {
				hi = mid-1;
			} else {
				list.add(mid,d);
				return;
			}
		}
		
		list.add(lo, d);
	}
	
	/*
	returns the average index of all slots that d can be placed
	*/
	
	public int findIndex(double d) {
		if (list.size() == 0) {
			return list.size();
		} else if (d <= list.get(0)) {
			return 0;
		} else if (d >= list.get(list.size()-1)) {
			return list.size();
		}
		
		int lo = 1;
		int hi = list.size()-1;
		while (lo != hi) {
			int mid = (lo+hi)/2;
			if (list.get(mid) < d) {
				lo = mid+1;
			} else if (list.get(mid-1) > d) {
				hi = mid-1;
			} else {
				return mid;
			}
		}
		
		return lo;
	}
	
	public double relativeIndex(double d) {
		int found = findIndex(d);
		
		int lo = found;
		int hi = found;
		while (lo > 0 && list.get(lo-1) == d) {
			lo--;
		}
		while (hi < list.size() && list.get(hi) == d) {
			hi++;
		}
		
		return (lo+hi+1)/(2*(double)(list.size()+1));
	}
	
	public Interval relativeInterval(double d) {
		int found = findIndex(d);
		
		int lo = found;
		int hi = found;
		while (lo > 0 && list.get(lo-1) == d) {
			lo--;
		}
		while (hi < list.size() && list.get(hi) == d) {
			hi++;
		}
		
		Interval out = new Interval(lo/((double)(list.size()+1)),(hi+1)/((double)(list.size()+1)));
		
		return out;
	}
	
	public int size() {
		return list.size();
	}
	
	public String toString() {
		return list.toString();
	}
}