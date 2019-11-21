package com.example.smartvalve;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.smartvalve.AllDevices.mID;

public class ThietBiAdapter extends BaseAdapter {

    public static String Id_Thiet_Bi;
    public static String name_device_pulic;
    ProgressDialog progressBar;

    Context myContext;
    int myLayout;
    List<ThietBi2> arrayThietBi;
    public ThietBiAdapter(Context context, int layout, ArrayList<ThietBi2> thietBiList)
    {
        myContext= context;
        myLayout= layout;
        arrayThietBi= thietBiList;
    }


    @Override
    public int getCount() {
        return arrayThietBi.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;

    }

    private class ViewHolder{
        TextView txtTenThietBi;}

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if ( convertView ==null) {
            LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(myLayout, null);
            holder = new ViewHolder();
            // ánh xạ
            holder.txtTenThietBi = (TextView) convertView.findViewById(R.id.txtTen);
            convertView.setTag(holder);
        }
        else
        {
            holder= (ViewHolder) convertView.getTag();
        }

        ThietBi2 thietBi= arrayThietBi.get(position);


        Id_Thiet_Bi="";
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Id_Thiet_Bi= mID.get(position);
                XuLy();
                //Toast.makeText(myContext, Id_Thiet_Bi, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(myContext,Control.class);
                ((AllDevices) myContext).startActivity(intent);
            }
        });

        holder.txtTenThietBi.setText(thietBi.getName());
        name_device_pulic=thietBi.getName();
        return convertView;
    }

    public void XuLy() {
        // Khởi tạo progressBar với đối là Context
        progressBar = new ProgressDialog(myContext);
        // Cho phép hủy progressBar nếu ấn nút Back
        progressBar.setCancelable(true);
        progressBar.onBackPressed();
        // Đặt tiêu đề cho ProgressBar
        progressBar.setMessage("Đang xử lý, vui lòng đợi...");
        // Đặt giao diện cho ProgressBar
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // Đặt giá trị đầu tiên, đây là giá trị thể hiện mức độ hoàn thành công
        // việc có thang từ 0 - > 100
        // do hiện tại công việc chưa hoàn thành được chút nào nên ta đặt là 0
        progressBar.setProgress(0);
        // Đặt cho giá trị lớn nhất thể hiện mức độ hoàn thành công việc là 100
        progressBar.setMax(100);
        // Hiện ProgressBar
        progressBar.show();

        // Tạo một luồng xử lý công việc.
        new ThietBiAdapter.MyThread().start();
    }

    class MyThread extends Thread {

        @Override
        public void run() {
            // xử lý công việc cần hoàn thành
            for (int i = 0; i < 30; i++) {
                // Tạm dừng 1s, thực tế thì chỗ này là xử lý công việc
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // tính xem công việc đã hoàn thành bao nhiêu phần trăm và đưa lên progressbar
                progressBar.setProgress((i * 100) / 30);
            }
            // đóng brogressbar.
            progressBar.dismiss();
        }
    }

}
