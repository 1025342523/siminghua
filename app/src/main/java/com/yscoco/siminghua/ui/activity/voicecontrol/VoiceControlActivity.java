package com.yscoco.siminghua.ui.activity.voicecontrol;

import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.ys.module.title.TitleBar;
import com.yscoco.siminghua.Constans;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.base.activity.BaseActivity;
import com.yscoco.siminghua.domain.BleDeviceBean;
import com.yscoco.siminghua.domain.MessageEvent;
import com.yscoco.siminghua.utils.SpUtils;
import com.yscoco.siminghua.utils.ThreadUtils;
import com.yscoco.siminghua.view.LongClickImageView;
import com.yscoco.siminghua.view.visualizers.RendererFactory;
import com.yscoco.siminghua.view.visualizers.WaveformView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZhangZeZhi on 2018-10-26.
 */

public class VoiceControlActivity extends BaseActivity {

    private static final String TAG = "AudioRecord";

    static final int SAMPLE_RATE_IN_HZ = 8000;

    static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
            AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);

    private AudioRecord mAudioRecord;
    private int seekBarValue = 0;

    private boolean isGetVoiceRun = false;
    private Object mLock;
    private static final int CAPTURE_SIZE = 256;

    @BindView(R.id.tb_title)
    TitleBar mTitleBar;

    @BindView(R.id.iv_battery)
    ImageView mIvBattery;

    @BindView(R.id.wfv)
    WaveformView mWfv;

    @BindView(R.id.iv_microphone)
    LongClickImageView mIvMicrophone;

    @BindView(R.id.range_seekbar)
    RangeSeekBar mRangeSeekBar;

    @BindView(R.id.iv_battery_two)
    ImageView mIvBatteryTwo;

    private boolean isFirstRun = false;
    private Visualizer mVisualizer;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mAudioRecord.startRecording();
            } catch (Exception e) {
                e.printStackTrace();
            }
            short[] buffer = new short[BUFFER_SIZE];
            while (isGetVoiceRun) {
                //r是实际读取的数据长度，一般而言r会小于buffersize
                int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
                long v = 0;
                // 将 buffer 内容取出，进行平方和运算
                for (int i = 0; i < buffer.length; i++) {
                    v += buffer[i] * buffer[i];
                }
                // 平方和除以数据总长度，得到音量大小。
                double mean = v / (double) r;
                double volume = 10 * Math.log10(mean);
                Log.e(TAG, "分贝值:" + volume);
                MessageEvent event = new MessageEvent();
                event.setCode(Constans.MICROPHONE_INPUT_CODE);
                event.setObj(volume);
                EventBus.getDefault().post(event);
                // 大概一秒十次
                synchronized (mLock) {
                    try {
                        mLock.wait(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (mAudioRecord != null) {
                try {
                    mAudioRecord.stop();
                    mAudioRecord.release();
                    mAudioRecord = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    };

    private List<BleDeviceBean> mBleDeviceBeanList;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_voice_control;
    }

    @Override
    protected void init() {

        getNoiseLevel();
        transparentStatusBar(R.color.transparent);
        mTitleBar.setTitle("声音控制");
        mTitleBar.gone();
        mBleDeviceBeanList = SpUtils.getDataList(Constans.BLUETOOTH_DEVICE_KEYS, BleDeviceBean.class);
        if (mBleDeviceBeanList != null && mBleDeviceBeanList.size() > 0) {
            boolean connected = BleManager.getInstance().isConnected(mBleDeviceBeanList.get(0).getBleDevice());
            if (connected) {
                mIvBattery.setVisibility(View.VISIBLE);
            }

            if (mBleDeviceBeanList.size() > 1 && BleManager.getInstance().isConnected(mBleDeviceBeanList.get(1).getBleDevice())) {
                mIvBatteryTwo.setVisibility(View.VISIBLE);
            }

        }

        mLock = new Object();
        RendererFactory rendererFactory = new RendererFactory();
        mWfv.setRenderer(rendererFactory.createSimpleWaveformRender(ContextCompat.getColor(this, R.color.yellow_color), Color.TRANSPARENT));

        mIvMicrophone.setOnImageViewClickedListener(new LongClickImageView.OnImageViewClickedListener() {
            @Override
            public void onDown() {
                if (!isGetVoiceRun) {
                    isGetVoiceRun = true;
                } else {
                    isGetVoiceRun = false;
                }
                mIvMicrophone.setImageResource(R.mipmap.icon_voice_contral_microphone_selected);
                Log.e("isGetVoiceRun", isGetVoiceRun + "");
                mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                        AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
//                if (!isFirstRun) {
                ThreadUtils.getThreadPool().execute(mRunnable);
//                    isFirstRun = true;
//                }
            }

            @Override
            public void onUp() {

                Log.e("isGetVoiceRun", isGetVoiceRun + "");

                mIvMicrophone.setImageResource(R.mipmap.icon_voice_contral_microphone);

                if (mBleDeviceBeanList != null && mBleDeviceBeanList.size() > 0) {

                    Log.e("onUp bleDeviceBean", mBleDeviceBeanList.get(0).getBleDevice().getMac());

                    BleManager.getInstance().write(mBleDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {

                            Log.e("onUp", justWrite[0] + "");

                        }

                        @Override
                        public void onWriteFailure(BleException exception) {

                        }

                    });

                }

                if (mBleDeviceBeanList != null && mBleDeviceBeanList.size() > 1) {

                    BleManager.getInstance().write(mBleDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {

                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {

                            Log.e("", justWrite[0] + "");

                        }

                        @Override
                        public void onWriteFailure(BleException exception) {

                        }

                    });

                }

            }

        });

        mRangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {

            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {

                seekBarValue = (int) leftValue;

            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

        });

    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onResume() {
        startVisualiser();
        super.onResume();
    }

    @OnClick({R.id.iv_left_arrow})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.iv_left_arrow:
                finish();
                break;
        }
    }

    // 设置音频线
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void startVisualiser() {
        mVisualizer = new Visualizer(0); // 初始化
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                if (mWfv != null) {
                    mWfv.setWaveform(waveform);
                }
                for (int i = 0; i < waveform.length; i++) {
                    Log.e("waveform", waveform[i] + "    " + i);
                }
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                Log.e("onFftDataCapture", String.valueOf(fft));
                if (fft != null) {
                    for (int j = 0; j < fft.length; j++) {
                        Log.e("fft", fft[j] + "");
                    }
                }
            }
        }, Visualizer.getMaxCaptureRate(), true, false);
        mVisualizer.setCaptureSize(CAPTURE_SIZE);
        mVisualizer.setEnabled(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onPause() {
        isGetVoiceRun = false;

        Log.e("onPause", "onPause");

        if (mBleDeviceBeanList != null && mBleDeviceBeanList.size() > 0) {

            Log.e("onPause", "onPause");

            BleManager.getInstance().write(mBleDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("onWriteSuccess1", justWrite[0] + "");
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            });
        }

        if (mBleDeviceBeanList != null && mBleDeviceBeanList.size() > 1) {

            Log.e("onPause", "onPause");

            BleManager.getInstance().write(mBleDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("onWriteSuccess2", justWrite[0] + "");
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            });
        }

        if (mVisualizer != null) {
            mVisualizer.setEnabled(false);
            mVisualizer.release();
        }
        super.onPause();
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getCode() == Constans.BLUE_BATTERY_ELECTRICITY) {
            byte[] data = (byte[]) event.getObj();
            changeImg(data[0], mIvBattery);
        } else if (event.getCode() == Constans.BLUE_BATTERY_ELECTRICITY_TWO) {
            byte[] data = (byte[]) event.getObj();
            changeImg(data[0], mIvBatteryTwo);
        } else if (event.getCode() == Constans.MICROPHONE_INPUT_CODE) {
            if (seekBarValue == 0) {
                return;
            }
            Double volume = (Double) event.getObj();
            volume += seekBarValue;
            if (volume >= 127) {
                volume = Double.valueOf(127);
            }
            byte volumes = Byte.parseByte(String.valueOf(volume.intValue()));
            byte[] bytes = new byte[256];

            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) (-128 + i % 20 + volumes % 20);
            }

            if (mWfv != null) {
                mWfv.setWaveform(bytes);
            }

            long value = (long) (volume % 3) * 10;
            long hexValue = Long.parseLong(String.valueOf(value), 16);
            if (mBleDeviceBeanList != null && mBleDeviceBeanList.size() > 0 && isGetVoiceRun) {
                BleManager.getInstance().write(mBleDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A201" + Long.parseLong(String.valueOf(hexValue), 16)), new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {

                        Log.e("onWriteSuccess1", justWrite[0] + "");

                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

                    }

                });

            } else {
                BleManager.getInstance().write(mBleDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {

                        Log.e("onWriteSuccess1", justWrite[0] + "");

                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

                    }

                });
            }

            if (mBleDeviceBeanList != null && mBleDeviceBeanList.size() > 1 && isGetVoiceRun) {

                BleManager.getInstance().write(mBleDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A201" + Long.parseLong(String.valueOf(hexValue), 16)), new BleWriteCallback() {

                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.e("onWriteSuccess", justWrite[0] + "");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

                    }

                });
            }/* else {
                BleManager.getInstance().write(mBleDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {

                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.e("onWriteSuccess2", justWrite[0] + "");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

                    }
                });
            }*/

        }

    }

    private void changeImg(byte datum, ImageView imageView) {
        int electricity = datum;
        if (80 <= electricity && electricity <= 100) {
            imageView.setImageResource(R.mipmap.icon_blue_battery_100);
        } else if (60 <= electricity && electricity < 80) {
            imageView.setImageResource(R.mipmap.icon_blue_battery_80);
        } else if (40 <= electricity && electricity < 60) {
            imageView.setImageResource(R.mipmap.icon_blue_battery_60);
        } else if (20 <= electricity && electricity < 40) {
            imageView.setImageResource(R.mipmap.icon_blue_battery_40);
        } else if (0 <= electricity && electricity < 20) {
            imageView.setImageResource(R.mipmap.icon_blue_battery_20);
        }
    }

    @Override
    protected void onDestroy() {
        if (mBleDeviceBeanList != null && mBleDeviceBeanList.size() > 0) {
            BleManager.getInstance().write(mBleDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("onWriteSuccess1", justWrite[0] + "");
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            });
        }

        if (mBleDeviceBeanList != null && mBleDeviceBeanList.size() > 1) {

            Log.e("onDestory", "onDestory");

            BleManager.getInstance().write(mBleDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("onWriteSuccess", justWrite[0] + "");
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            });
        }

        super.onDestroy();
    }

    public void getNoiseLevel() {
        if (isGetVoiceRun) {
            Log.e(TAG, "还在录着呢");
            return;
        }
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        ThreadUtils.getThreadPool().execute(mRunnable);
    }

}
