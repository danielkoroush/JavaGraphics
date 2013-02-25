
import java.util.Vector;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.FileWriter;
public class QuickSort{
	
	public static void main(String args[]){
Vector L=new Vector();

Point temp=new Point(1,50);
L.add(temp);

 temp=new Point(34,15);
L.add(temp);

temp=new Point(-1,45);
L.add(temp);

temp=new Point(14,23);
L.add(temp);

temp=new Point(4345,87);
L.add(temp);

temp=new Point(71,345);
L.add(temp);

temp=new Point(41,-5);
L.add(temp);

temp=new Point(11,0);
L.add(temp);

QuickSort q=new QuickSort();
L=q.quickSortY(L);

for (int i=0;i<L.size();i++){
	System.out.println(L.elementAt(i));
}
}
	
	

 public double [] quickSort(double [] elements){ 
quickSort2(elements, 0, elements.length-1);
return elements;
}
 
 public Vector quickSortX(Vector elements){ 
 	quickSortX(elements, 0, elements.size()-1);
 	return elements;
 	}
 public Vector quickSortY(Vector elements){ 
 	quickSortY(elements, 0, elements.size()-1);
 	return elements;
 	}
 public int Exists(Vector list, Point p){
 	MapPoint temp;
 	for (int i=0;i<list.size();i++){
 		temp=(MapPoint) list.elementAt(i);
 		if (((temp.location).getX()==p.getX()) && ((temp.location).getY()==p.getY())){
 			return i;
 		}
 	}
 	return -1;
 }
 public Vector deleteElement(Vector list, Point p){
 	MapPoint temp;
 	if (list==null) return null;
 	for (int i=0;i<list.size();i++){
 		temp=(MapPoint) list.elementAt(i);
 		if (((temp.location).getX()==p.getX()) && ((temp.location).getY()==p.getY())){
 			list.removeElementAt(i);
 			return list;
 		}
 	}
 	return list;
 }
 
public Vector insert(Vector L,MapPoint p,int i,int j){

MapPoint temp;
if (i-j==0){
L.add(p);
}else if (j-i==1){
   temp=(MapPoint) L.elementAt(i);

   if (temp.getCost()<p.getCost()){
		if (i+1>=L.size()){
			L.add(p);
		}else{
			L.add(i+1,p);
		}
	}else{
		L.add(i,p);
	}
}else{
  int x=(int)((j+i)/2);
 temp=(MapPoint) L.elementAt(x);
if (temp.getCost()<=p.getCost()){

	L=insert(L,p,x,L.size());
}else if (temp.getCost()>p.getCost()){

        L=insert(L,p,i,x);
   } 

}
return L;

}
public int Search(Vector L,MapPoint p){
    MapPoint temp; 
	for (int i=0;i<L.size();i++){
     	temp=(MapPoint) L.elementAt(i);
     	if ((temp.getX()==p.getX()) && (temp.getY()==p.getY()))
     		return i;
     }
	return -1;
}

public int Position(Vector L,MapPoint p,int i,int j){

MapPoint temp;
if (i-j==0){
return -1;
}else if (j-i==1){
   temp=(MapPoint) L.elementAt(i);

   if ((temp.getX()==p.getX())&&(temp.getY()==p.getY())){
		return i;
	}else{
		return -1;
	}
}else{
  int x=(int)((j+i)/2);
 temp=(MapPoint) L.elementAt(x);
if (temp.getCost()<=p.getCost()){

	return (Position(L,p,x,L.size()));
}else if (temp.getCost()>p.getCost()){

        return(Position(L,p,i,x));
   } 

}
return -1;

}


 private static void quickSort3(Vector elements, int lowIndex, int highIndex){
  	int lowToHighIndex;
    int highToLowIndex;
    int pivotIndex;
    MapPoint pivotValue;  
    MapPoint lowToHighValue;
    MapPoint highToLowValue;
    MapPoint parking;
    int newLowIndex;
    int newHighIndex;
    int compareResult;

    lowToHighIndex = lowIndex;
    highToLowIndex = highIndex;

    pivotIndex = (lowToHighIndex + highToLowIndex) / 2;
    pivotValue =(MapPoint) elements.elementAt(pivotIndex);

    newLowIndex = highIndex + 1;
    newHighIndex = lowIndex - 1;
    // loop until low meets high
    while ((newHighIndex + 1) < newLowIndex) { // loop from low to high to find a candidate for swapping
      lowToHighValue = (MapPoint) elements.elementAt(lowToHighIndex);
      while ((lowToHighIndex < newLowIndex) && ((lowToHighValue.totalCost).doubleValue()<(pivotValue.totalCost).doubleValue()) )
      { newHighIndex = lowToHighIndex; // add element to lower part
        lowToHighIndex ++;
        lowToHighValue =(MapPoint) elements.elementAt(lowToHighIndex);
      }

      // loop from high to low find other candidate for swapping
      highToLowValue = (MapPoint) elements.elementAt(highToLowIndex);
      while ((newHighIndex <= highToLowIndex) && ((highToLowValue.totalCost).doubleValue()>(pivotValue.totalCost).doubleValue())) { 
      	newLowIndex = highToLowIndex; // add element to higher part
        highToLowIndex --;
        highToLowValue = (MapPoint) elements.elementAt(highToLowIndex);
      }

      // swap if needed
      if (lowToHighIndex == highToLowIndex) // one last element, may go in either part
      { newHighIndex = lowToHighIndex; // move element arbitrary to lower part
      }
      else if (lowToHighIndex < highToLowIndex) // not last element yet
      { 
        if ((lowToHighValue.totalCost).doubleValue()>=((highToLowValue.totalCost).doubleValue())) 
        { parking = lowToHighValue;
          elements.setElementAt(highToLowValue,lowToHighIndex);
          elements.setElementAt(parking,highToLowIndex);
          
         newLowIndex = highToLowIndex;
          newHighIndex = lowToHighIndex;

          lowToHighIndex ++;
          highToLowIndex --;
        }
      }
    }

    if (lowIndex < newHighIndex)
    { quickSort3(elements, lowIndex, newHighIndex); // sort lower subpart
    }
    if (newLowIndex < highIndex)
    {  quickSort3(elements, newLowIndex, highIndex); // sort higher subpart
    }
  }

 private static void quickSortX(Vector elements, int lowIndex, int highIndex){
  	int lowToHighIndex;
    int highToLowIndex;
    int pivotIndex;
    Point pivotValue;  
    Point lowToHighValue;
    Point highToLowValue;
    Point parking;
    int newLowIndex;
    int newHighIndex;
    int compareResult;

    lowToHighIndex = lowIndex;
    highToLowIndex = highIndex;

    pivotIndex = (lowToHighIndex + highToLowIndex) / 2;
    pivotValue =(Point) elements.elementAt(pivotIndex);

    newLowIndex = highIndex + 1;
    newHighIndex = lowIndex - 1;
    // loop until low meets high
    while ((newHighIndex + 1) < newLowIndex) { // loop from low to high to find a candidate for swapping
      lowToHighValue = (Point) elements.elementAt(lowToHighIndex);
      while ((lowToHighIndex < newLowIndex) && ((lowToHighValue.x)<(pivotValue.x)) )
      { newHighIndex = lowToHighIndex; // add element to lower part
        lowToHighIndex ++;
        lowToHighValue =(Point) elements.elementAt(lowToHighIndex);
      }

      // loop from high to low find other candidate for swapping
      highToLowValue = (Point) elements.elementAt(highToLowIndex);
      while ((newHighIndex <= highToLowIndex) && ((highToLowValue.x)>(pivotValue.x))) { 
      	newLowIndex = highToLowIndex; // add element to higher part
        highToLowIndex --;
        highToLowValue = (Point) elements.elementAt(highToLowIndex);
      }

      // swap if needed
      if (lowToHighIndex == highToLowIndex) // one last element, may go in either part
      { newHighIndex = lowToHighIndex; // move element arbitrary to lower part
      }
      else if (lowToHighIndex < highToLowIndex) // not last element yet
      { 
        if ((lowToHighValue.x)>=((highToLowValue.x))) 
        { parking = lowToHighValue;
          elements.setElementAt(highToLowValue,lowToHighIndex);
          elements.setElementAt(parking,highToLowIndex);
          
         newLowIndex = highToLowIndex;
          newHighIndex = lowToHighIndex;

          lowToHighIndex ++;
          highToLowIndex --;
        }
      }
    }

    if (lowIndex < newHighIndex){ 
    	quickSortX(elements, lowIndex, newHighIndex); // sort lower subpart
    }
    if (newLowIndex < highIndex){ 
    	quickSortX(elements, newLowIndex, highIndex); // sort higher subpart
    }
  }
 
 private static void quickSortY(Vector elements, int lowIndex, int highIndex){
  	int lowToHighIndex;
    int highToLowIndex;
    int pivotIndex;
    Point pivotValue;  
    Point lowToHighValue;
    Point highToLowValue;
    Point parking;
    int newLowIndex;
    int newHighIndex;
    int compareResult;

    lowToHighIndex = lowIndex;
    highToLowIndex = highIndex;

    pivotIndex = (lowToHighIndex + highToLowIndex) / 2;
    pivotValue =(Point) elements.elementAt(pivotIndex);

    newLowIndex = highIndex + 1;
    newHighIndex = lowIndex - 1;
    // loop until low meets high
    while ((newHighIndex + 1) < newLowIndex) { // loop from low to high to find a candidate for swapping
      lowToHighValue = (Point) elements.elementAt(lowToHighIndex);
      while ((lowToHighIndex < newLowIndex) && ((lowToHighValue.y)<(pivotValue.y)) )
      { newHighIndex = lowToHighIndex; // add element to lower part
        lowToHighIndex ++;
        lowToHighValue =(Point) elements.elementAt(lowToHighIndex);
      }

      // loop from high to low find other candidate for swapping
      highToLowValue = (Point) elements.elementAt(highToLowIndex);
      while ((newHighIndex <= highToLowIndex) && ((highToLowValue.y)>(pivotValue.y))) { 
      	newLowIndex = highToLowIndex; // add element to higher part
        highToLowIndex --;
        highToLowValue = (Point) elements.elementAt(highToLowIndex);
      }

      // swap if needed
      if (lowToHighIndex == highToLowIndex) // one last element, may go in either part
      { newHighIndex = lowToHighIndex; // move element arbitrary to lower part
      }
      else if (lowToHighIndex < highToLowIndex) // not last element yet
      { 
        if ((lowToHighValue.y)>=((highToLowValue.y))) 
        { parking = lowToHighValue;
          elements.setElementAt(highToLowValue,lowToHighIndex);
          elements.setElementAt(parking,highToLowIndex);
          
         newLowIndex = highToLowIndex;
          newHighIndex = lowToHighIndex;

          lowToHighIndex ++;
          highToLowIndex --;
        }
      }
    }

    if (lowIndex < newHighIndex){ 
    	quickSortY(elements, lowIndex, newHighIndex); // sort lower subpart
    }
    if (newLowIndex < highIndex){ 
    	quickSortY(elements, newLowIndex, highIndex); // sort higher subpart
    }
  }
 
  private static void quickSort2(double [] elements, int lowIndex, int highIndex){
  	int lowToHighIndex;
    int highToLowIndex;
    int pivotIndex;
    double pivotValue;  
    double lowToHighValue;
    double highToLowValue;
    double parking;
    int newLowIndex;
    int newHighIndex;
    int compareResult;

    lowToHighIndex = lowIndex;
    highToLowIndex = highIndex;
    /** Choose a pivot, remember it's value
     *  No special action for the pivot element itself.
     *  It will be treated just like any other element.
     */
    pivotIndex = (lowToHighIndex + highToLowIndex) / 2;
    pivotValue = elements[pivotIndex];

    newLowIndex = highIndex + 1;
    newHighIndex = lowIndex - 1;
    // loop until low meets high
    while ((newHighIndex + 1) < newLowIndex) { // loop from low to high to find a candidate for swapping
      lowToHighValue = elements[lowToHighIndex];
      while ((lowToHighIndex < newLowIndex) && (lowToHighValue<pivotValue) )
      { newHighIndex = lowToHighIndex; // add element to lower part
        lowToHighIndex ++;
        lowToHighValue = elements[lowToHighIndex];
      }

      // loop from high to low find other candidate for swapping
      highToLowValue = elements[highToLowIndex];
      while ((newHighIndex <= highToLowIndex) && (highToLowValue>pivotValue))
        
      { newLowIndex = highToLowIndex; // add element to higher part
        highToLowIndex --;
        highToLowValue = elements[highToLowIndex];
      }

      // swap if needed
      if (lowToHighIndex == highToLowIndex) // one last element, may go in either part
      { newHighIndex = lowToHighIndex; // move element arbitrary to lower part
      }
      else if (lowToHighIndex < highToLowIndex) // not last element yet
      { 
        if (lowToHighValue>=(highToLowValue)) // low >= high, swap, even if equal
        { parking = lowToHighValue;
          elements[lowToHighIndex]= highToLowValue;
          elements[highToLowIndex]= parking;

          newLowIndex = highToLowIndex;
          newHighIndex = lowToHighIndex;

          lowToHighIndex ++;
          highToLowIndex --;
        }
      }
    }

    // Continue recursion for parts that have more than one element
    if (lowIndex < newHighIndex)
    { quickSort2(elements, lowIndex, newHighIndex); // sort lower subpart
    }
    if (newLowIndex < highIndex)
    {  quickSort2(elements, newLowIndex, highIndex); // sort higher subpart
    }
  }
}