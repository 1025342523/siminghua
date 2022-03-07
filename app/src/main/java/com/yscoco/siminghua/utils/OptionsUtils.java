package com.yscoco.siminghua.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.yscoco.siminghua.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by ZhangZeZhi on 2018-09-29.
 */

public class OptionsUtils {

    private OptionsPickerView<String> mPvOptions;

    public static OptionsUtils getInstance() {
        return new OptionsUtils();
    }

    public void createOptions(Activity activity, final TextView view, final List<String> list, final List<List<String>> list1, final List<List<List<String>>> list2, int i1, int i2, int i3, final String title) {
        final StringBuffer sb = new StringBuffer();

        mPvOptions = new OptionsPickerBuilder(activity, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String text = null;
                if (list != null && list.size() > 0 && list1 == null && list2 == null) {
                    text = list.get(options1);
                } else if (list != null && list.size() > 0 && list1 != null && list1.size() > 0 && list2 == null) {
                    text = sb.append(list.get(options1)).append(" ").append(list1.get(options1).get(options2)).toString();
                } else if (list != null && list.size() > 0 && list1 != null && list1.size() > 0 && list2 != null && list2.size() > 0) {
                    text = sb.append(list.get(options1)).append(" ").append(list1.get(options1).get(options2)).append(" ").append(list2.get(options1).get(options2).get(options3)).toString();
                }
                if (view != null) {
                    view.setText(text);
                }
                if (mListener != null) {
                    mListener.onPickerText(text);
                }
            }
        }).setLayoutRes(R.layout.layout_custom_options, new CustomListener() {
            @Override
            public void customLayout(View v) {
                TextView tvCancel = (TextView) v.findViewById(R.id.tv_cancel);
                TextView tvOk = (TextView) v.findViewById(R.id.tv_ok);
                TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);
                tvTitle.setText(title);
                tvOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPvOptions.returnData();
                        mPvOptions.dismiss();
                    }
                });

                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPvOptions.dismiss();
                    }
                });

            }
        })
                .setContentTextSize(16)
                .setSelectOptions(i1, i2, i3)
                .setBgColor(Color.WHITE)
                .setTextColorCenter(Color.parseColor("#333333"))
                .isRestoreItem(true)
                .setDividerColor(Color.parseColor("#EEEEEE"))
                .setLineSpacingMultiplier(2.0f)
                .isCenterLabel(false)
                .setBackgroundId(0x00000000)
                .isDialog(false)
                .build();
        if (list != null && list.size() > 0 && list1 == null && list2 == null) {
            mPvOptions.setPicker(list);
        } else if (list != null && list.size() > 0 && list1 != null && list1.size() > 0 && list2 == null) {
            mPvOptions.setPicker(list, list1);
        } else if (list != null && list.size() > 0 && list1 != null && list1.size() > 0 && list2 != null && list2.size() > 0) {
            mPvOptions.setPicker(list, list1, list2);
        }
        mPvOptions.show();
    }

    public void dateOptions(Activity activity, final TextView view) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        DatePickerDialog dialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Calendar instance = Calendar.getInstance(Locale.CHINA);
                instance.set(i, i1, i2);
                if (view != null) {
                    view.setText(DateUtil.getDate(instance.getTimeInMillis(), "yyyy-MM-dd"));
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private OnSelectTextListener mListener;

    public void setOnSelectTextListener(OnSelectTextListener l) {
        mListener = l;
    }

    public interface OnSelectTextListener {
        void onPickerText(String text);
    }

}
