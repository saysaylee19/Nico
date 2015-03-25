package com.nicotrax.nicotrax.fragment.dashBoard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.nicotrax.nicotrax.R;

import java.util.ArrayList;

public class PieFragment extends Fragment {
    //Name of Slices
    String[] mParties = new String[] {
            "Smoked", "UnSmoked"
    };
    private PieChart mChart;
    private Typeface tf;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_pie, container, false);
        // Inflate the layout for this fragment

        Button dec_button = (Button) rootView.findViewById(R.id.Dec_button);
        Button inc_button = (Button) rootView.findViewById(R.id.Inc_button);

        mChart = (PieChart) rootView.findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);

        // change the color of the center-hole
        // mChart.setHoleColor(Color.rgb(235, 235, 235));
        mChart.setHoleColorTransparent(true);

        tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");

        mChart.setCenterTextTypeface(Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf"));

        mChart.setHoleRadius(65f);
        //mChart.setTransparentCircleRadius(65f);

        mChart.setDescription("");

        mChart.setDrawCenterText(true);

        mChart.setDrawHoleEnabled(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        //mChart.setOnChartValueSelectedListener(this);
        mChart.setTouchEnabled(false);

        //Initially Count of Cigarettes is constant as 7
        mChart.setCenterText("7");
        mChart.setCenterTextSize(30f);

        //Since we have only two slices, count= 1 as range starts from 0..currently max=20
        setData(1,7,20);

        mChart.animateXY(1500, 1500);
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);

        l.setEnabled(false);


        //OnClickListeners for increment and decrement buttons
        inc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(v.getId()==R.id.Inc_button){
                    showAlertBox("Add Cigarette",getActivity().getBaseContext(),1);
                }
            }
        });

        dec_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(v.getId()==R.id.Dec_button){
                    showAlertBox("Subtract Cigarette",getActivity().getBaseContext(),0);
                }
            }
        });


        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        //Initially code here
    }
    //MPChart sets data here
    private void setData(int count, float range,float total) {
        float mult = range;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
       // for (int i = 0; i < count + 1; i++) {
         //   yVals1.add(new Entry((float) (Math.random() * mult) + mult / 5, i));
        //}

        yVals1.add(new Entry((float)(range/total)*100,0));
        yVals1.add(new Entry((float)(100-(range/total)*100),0));


        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count + 1; i++)
            xVals.add(mParties[i % mParties.length]);

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        /*for (int c : ColorTemplate.VORDIPLOM_COLORS)
        //    colors.add(c);
        //A1F7D3 E9E9E9

       //for (int c : ColorTemplate.JOYFUL_COLORS)
        //   colors.add(c);

       // for (int c : ColorTemplate.COLORFUL_COLORS)
       //     colors.add(c);

        //for (int c : ColorTemplate.LIBERTY_COLORS)
          //  colors.add(c);


        /*for (int c : ColorTemplate.PASTEL_COLORS) {
            System.out.println("--->color " + c);
            colors.add(c);
        }
        */

        Resources res=getResources();
        colors.add(res.getColor(R.color.background_color));
        colors.add(res.getColor(R.color.grey));

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(tf);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }
    //Popup alert
    private void showAlertBox(String msg,Context context, final int identify){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Code that is executed when clicking YES
                if(identify==1)
                    incrementCount();
                else
                    decrementCount();
                dialog.dismiss();
            }

        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Code that is executed when clicking NO
                dialog.dismiss();
            }

        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    //Increment Cigarette count
    private void incrementCount(){
        String c=mChart.getCenterText();
        int count=Integer.parseInt(c);
        count++;
        mChart.setCenterText("" + count);
        setData(1,count,20);
    }
    //Decrement Cigarette count
    private  void decrementCount(){
        String c=mChart.getCenterText();
        int count=Integer.parseInt(c);
        count--;
        mChart.setCenterText("" + count);
        setData(1,count,20);
    }


}
