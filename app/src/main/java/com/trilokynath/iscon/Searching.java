package com.trilokynath.iscon;

import java.util.*;
import java.util.regex.Pattern;

public class Searching{

    public ArrayList<DATA> onSearch(ArrayList<DATA> hrList,String search){

        ArrayList<FIND> add = new ArrayList<>();

        search = search.toLowerCase();

        Pattern p = Pattern.compile("\\s*(\\s|-|,)\\s*");

        //searching by city
        int index = 0;
        for(DATA hr : hrList) {
            add.add(new FIND(index, hr.getAddress().toLowerCase(), hr.getName().toLowerCase(), hr.getMobile()));
            index++;
        }

        String input[] = p.split(search);

        index=0;
        for(FIND find : add){
            String mainAdd[] = p.split(find.address);
            String[] name = p.split(find.name);

            //search by city
            for(String s : mainAdd){
                for (String anInput : input) {
                    if (s.contains(anInput)) {
            //            System.out.println("accuracy : "+data.accuracy);
                        find.accuracy++;
                        add.set(index, find);
                    }
                }
            }

            //search by name
            for(String s : name){
                for (String anInput : input) {
                    if (s.contains(anInput)) {
            //            System.out.println("accuracy : "+data.accuracy);
                        find.accuracy++;
                        add.set(index, find);
                    }
                }
            }

            //search by mobile
            if (find.mobile.contains(search)) {
                find.accuracy++;
                add.set(index, find);
            }

            index++;
        }



        Collections.sort(add, new Comparator<FIND>(){
            @Override
            public int compare(FIND find, FIND t1) {
                return (t1.accuracy-find.accuracy);
            }
        });

    //    for(DATA d : add) {
    //        System.out.println("Address: '"+d.address+"'\tAccuracy: '"+d.accuracy+"'");
    //    }

        ArrayList<DATA> newData = new ArrayList<>();
        index = 0;
        for(FIND find : add){
        //    Log.d("DATA", data.name+" Accuracy:"+data.accuracy);
            if(find.accuracy>0) {
                newData.add(hrList.get(find.ID));
                index++;
            }
        }
        return newData;
    }

}
class FIND{
    int ID;
    String address;
    int accuracy = 0;
    String name;
    String mobile;

    FIND(int ID, String address,String name, String mobile){
        this.address = address;
        this.ID = ID;
        this.name = name;
        this.mobile = mobile;
    }
}