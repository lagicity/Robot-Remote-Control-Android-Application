package com.example.tinghwee.group8;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    // Debugging //
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler //
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler //
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes //
    private static final int REQUEST_CONNECT_DEVICE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Name of the connected device //
    private String mConnectedDeviceName = null;

    // String buffer for outgoing messages //
    private StringBuffer mOutStringBuffer;
    private BluetoothService bluetoothService = null;
    // Local Bluetooth adapter //
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services //
    private BluetoothService mChatService = null;

    Handler myHandler = new Handler();
    JSONObject jsonObject;

    // Map/Arena Declarations //
    private Map Arena;
    private RelativeLayout mapDisplay;
    private String gridString;

    private int[] intArray = new int[300];
    ArrayList<Integer> obstacleArray = new ArrayList<>();

    //String declaration
    String decodedString, amdDecoded;
    String[][] obsArray = new String[15][20];

    ArrayList obstacleSensor = new ArrayList();
    ArrayList unexploredSensor = new ArrayList();
    ArrayList exploredSensor = new ArrayList();
    ArrayList<Integer> waypointArray = new ArrayList<>();

    // F1, F2, Up, Down, Left, Right Configurations Info //
    SharedPreferences preferred;

    // Integer variables for default robot position // (FOR AMD)
    Integer xStatus = 0;
    Integer yStatus = 17;
    Integer dStatus = 0;

    private long startTime = 0L;
    private long startTime1 = 0L;

    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    long timeInMilliseconds1 = 0L;
    long timeSwapBuff1 = 0L;
    long updatedTime1 = 0L;

    // Fp //
    String direction_ = "";
    int run = 0;

    // Voice //
    private static final int SPEECH_REQUEST_CODE = 0;

    // Auto Keys //
    private boolean autoUp = false;
    private boolean autoDown = false;
    private boolean autoLeft = false;
    private boolean autoRight = false;

    private final long REPEAT_DELAY = 1000;
    private Handler repeatUpdateHandler = new Handler();

    //Timer to update map
    private Timer timer;
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            sendMessage(preferred.getString("SendArenaCommand", ""));
        }
    };

    static int lastOpenedTab = 0;

    //Control panel UI
    private LinearLayout mainLayoutMain;
    private LinearLayout mainLayoutAMD;
    private LinearLayout mainLayoutMDF;
    private LinearLayout mainLayoutCoord;
    private LinearLayout mainLayoutFeatures;
    private LinearLayout mainLayoutStart;

    //Animations
    private Animation animSlideUp;
    private Animation animSlideDown;

    //RHS Menu
    private Button RHSButtonMain;
    private Button RHSButtonAMD;
    private Button RHSButtonMDF;
    private Button RHSButtonCoord;
    private Button RHSButtonStart;
    private Button RHSButtonFeatures;

    /**
     * TABS
     */
    //Main Tab
    private ImageButton controlButtonUp;
    private ImageButton controlButtonLeft;
    private ImageButton controlButtonRight;
    private ImageButton controlButtonDown;

    private Switch mainSwitchAutoManual;

    private Button mainButtonUpdate;
    private Button mainButtonF1;
    private Button mainButtonF2;

    private TextView mainTextViewRobotStatus;


    // AMD Tab
    private ListView amdListViewSend;
    private ListView amdListViewReceive;

    private ArrayAdapter<String> amdArrayAdapterSend;
    private ArrayAdapter<String> amdArrayAdapterReceive;

    private EditText amdEditTextSendText;
    private Button amdSendButton;


    //MDF Tab
    private TextView mdfTextViewExploredObstacle;
    private TextView mdfTextViewExplored;

    //Coordinates Tab
    private boolean robotDragged;
    private ImageView coordImageViewRobot;

    private Button coordButtonRobotSet;
    private Button coordButtonRobotReset;
    private Button coordButtonWpSet;
    private Button coordButtonWpReset;

    private Spinner coordSpinnerWpRow;
    private Spinner coordSpinnerWpColumn;
    private TextView coordTextViewRobotRow;
    private TextView coordTextViewRobotColumn;

    private Spinner coordSpinnerRobotDirection;

    //Features Tab
    private Switch featuresSwitchTilt;
    private TextView featuresTextViewSpokenText;
    private ImageButton featuresButtonVoice;
    private Button featuresButtonGoToCommands;
    private String spokenText;
    private boolean autoUpdate = true;
    private boolean tiltMode = false;
    private int pre_state = 0;

    private long lastUpdateTime = 0;

    //Start Tab
    private ToggleButton startToggleButtonExplore;
    private ToggleButton startToggleButtonFastest;

    private TextView startTextViewExploreTime;
    private TextView startTextViewFastestTime;

    private Button startButtonReset;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // record data in the SettingActivity //
        preferred = PreferenceManager.getDefaultSharedPreferences(this);

        //Animations
        animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.main_anim_slide_up);
        animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.main_anim_slide_down);

        //RHS Menu
        RHSButtonMain = (Button) findViewById(R.id.rhs_button_main);
        RHSButtonAMD = (Button) findViewById(R.id.rhs_button_amd);
        RHSButtonMDF = (Button) findViewById(R.id.rhs_button_mdf);
        RHSButtonCoord = (Button) findViewById(R.id.rhs_button_coord);
        RHSButtonStart = (Button) findViewById(R.id.rhs_button_start);
        RHSButtonFeatures = (Button) findViewById(R.id.rhs_button_features);

        //Control Panel to control robot
        controlButtonUp = (ImageButton) findViewById(R.id.control_button_up);
        controlButtonDown = (ImageButton) findViewById(R.id.control_button_down);
        controlButtonLeft = (ImageButton) findViewById(R.id.control_button_left);
        controlButtonRight = (ImageButton) findViewById(R.id.control_button_right);

        // Main Tab
        mainLayoutMain = (LinearLayout) findViewById(R.id.main_layout_tab_main);
        mainLayoutAMD = (LinearLayout) findViewById(R.id.main_layout_tab_amd);
        mainLayoutMDF = (LinearLayout) findViewById(R.id.main_layout_tab_mdf);
        mainLayoutCoord = (LinearLayout) findViewById(R.id.main_layout_tab_coord);
        mainLayoutFeatures = (LinearLayout) findViewById(R.id.main_layout_tab_features);
        mainLayoutStart = (LinearLayout) findViewById(R.id.main_layout_tab_start);

        mainSwitchAutoManual = (Switch) findViewById(R.id.main_switch_automanual);

        mainButtonUpdate = (Button) findViewById(R.id.main_button_update);
        mainButtonF1 = (Button) findViewById(R.id.main_button_f1);
        mainButtonF2 = (Button) findViewById(R.id.main_button_f2);

        mainTextViewRobotStatus = (TextView) findViewById(R.id.main_textview_robot_status);

        //AMD Tab
        amdEditTextSendText = (EditText) findViewById(R.id.amd_edittext_send_text);
        amdEditTextSendText.setOnEditorActionListener(amdListenerSend);

        //MDF Tab
        mdfTextViewExploredObstacle = (TextView) findViewById(R.id.mdf_explored_obstacle);
        mdfTextViewExplored = (TextView) findViewById(R.id.mdf_explored);


        // Coordinates Tab
        coordImageViewRobot = (ImageView) findViewById(R.id.coord_imageview_robot);

        coordButtonRobotSet = (Button) findViewById(R.id.coord_button_robot_set);
        coordButtonRobotReset = (Button) findViewById(R.id.coord_button_robot_reset);
        coordButtonWpSet = (Button) findViewById(R.id.coord_button_wp_set);
        coordButtonWpReset = (Button) findViewById(R.id.coord_button_wp_reset);

        coordSpinnerWpRow = (Spinner) findViewById(R.id.coord_spinner_wp_row);
        coordSpinnerWpColumn = (Spinner) findViewById(R.id.coord_spinner_wp_column);
        coordTextViewRobotRow = (TextView) findViewById(R.id.coord_textview_robot_row);
        coordTextViewRobotColumn = (TextView) findViewById(R.id.coord_textview_robot_column);

        coordSpinnerRobotDirection = (Spinner) findViewById(R.id.coord_spinner_robot_direction);

        // Start Tab
        startToggleButtonExplore = (ToggleButton) findViewById(R.id.start_togglebutton_btn_explore);
        startToggleButtonFastest = (ToggleButton) findViewById(R.id.start_togglebbutton_btn_fastest);

        startTextViewExploreTime = (TextView) findViewById(R .id.start_textview_timer_explore);
        startTextViewFastestTime = (TextView) findViewById(R.id.start_textview_timer_fastest);

        startButtonReset = (Button) findViewById(R.id.start_button_reset);

        //Features Tab
        featuresSwitchTilt = (Switch) findViewById(R.id.features_switch_tilt);
        featuresTextViewSpokenText = (TextView) findViewById(R.id.features_textview_spokentext);
        featuresButtonVoice = (ImageButton) findViewById(R.id.features_button_voice);
        featuresButtonGoToCommands = (Button) findViewById(R.id.features_button_go_to_commands);

        // initialize the map //
        init();

        // Get device's accelerometer data
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        System.out.println(sensorManager);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // adapter is null > Bluetooth not available
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Handler for long continuous clicks-------------------------------------------------------
        class RepetitiveUpdater implements Runnable {
            @Override
            public void run() {
                if (autoRight) {
                    turningRight();
                    repeatUpdateHandler.postDelayed(new RepetitiveUpdater(), REPEAT_DELAY);
                } else if (autoLeft) {
                    turningLeft();
                    repeatUpdateHandler.postDelayed(new RepetitiveUpdater(), REPEAT_DELAY);
                } else if (autoUp) {
                    straight();
                    repeatUpdateHandler.postDelayed(new RepetitiveUpdater(), REPEAT_DELAY);
                } else if (autoDown) {
                    turningRight();
                    turningRight();
                    repeatUpdateHandler.postDelayed(new RepetitiveUpdater(), REPEAT_DELAY);
                }
            }
        }

        // F1, F2 buttons
        mainButtonF1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage(preferred.getString("F1Command", ""));
            }
        });
        mainButtonF2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage(preferred.getString("F2Command", ""));
            }
        });

        // Initialize the send button with a listener that for click events, Up, Left, Right, Down //
        controlButtonUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage(preferred.getString("UpCommand", ""));
                straight();
            }
        });
        controlButtonLeft.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage(preferred.getString("LeftCommand", ""));
                turningLeft();
            }
        });
        controlButtonRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage(preferred.getString("RightCommand", ""));
                turningRight();
            }
        });
        controlButtonDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage(preferred.getString("DownCommand", ""));
                turningRight();
                turningRight();
            }
        });

        //Button control for Control Tab------------------------------------------------------------
        controlButtonUp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                autoUp = true;
                repeatUpdateHandler.post(new RepetitiveUpdater());
                return false;
            }
        });
        controlButtonUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && autoUp) {
                    autoUp = false;
                }
                return false;
            }
        });
        controlButtonDown.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                autoDown = true;
                repeatUpdateHandler.post(new RepetitiveUpdater());
                return false;
            }
        });
        controlButtonDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && autoDown) {
                    autoDown = false;
                }
                return false;
            }
        });
        controlButtonRight.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                autoRight = true;
                repeatUpdateHandler.post(new RepetitiveUpdater());
                return false;
            }
        });
        controlButtonRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && autoRight) {
                    autoRight = false;
                }
                return false;
            }
        });
        controlButtonLeft.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                autoLeft = true;
                repeatUpdateHandler.post(new RepetitiveUpdater());
                return false;
            }
        });
        controlButtonLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && autoLeft) {
                    autoLeft = false;
                }
                return false;
            }
        });

        //Manual Mode-------------------------------------------------------------------------------
        mainButtonUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (obstacleArray != null) {
                        sendMessage(preferred.getString("SendArenaCommand", ""));
                        Arena.setObstacleArray(obstacleArray);
                        autoUpdate = true;
                    }
                    if (obsArray != null) {
                        autoUpdate = true;
                        Arena.setObstacleArray(obstacleSensor);
                    }
                    if (decodedString != null) {
                        autoUpdate = true;
                        updateGridArray(toIntArray(decodedString));
                    }
                    autoUpdate = false;
                    showToastMessage("Map Updated");
                } catch (Exception e) {

                    // AMD obstacleListArray // manual update of map
                    if (amdDecoded != null) {
                        autoUpdate = true;
                        updateGridArray(toIntArray(amdDecoded));
                    }
                    showToastMessage("Already Updated");
                }
            }
        });

        if (autoUpdate) { //switching mode
            mainButtonUpdate.setBackgroundResource(R.drawable.roundedbutton);
            if (mConnectedDeviceName != null) {
                sendMessage(preferred.getString("SendArenaCommand", ""));
            }
            showToastMessage("Auto Update: ON");
            mainSwitchAutoManual.setChecked(true);
        }

        mainSwitchAutoManual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mainButtonUpdate.setEnabled(false);
                    mainButtonUpdate.setBackgroundResource(R.drawable.roundedbutton);
                    autoUpdate = true;

                        if (mConnectedDeviceName != null && timer == null) {
                            //timer to update obstacles
                            timer = new Timer();
                            timer.schedule(timerTask, 0, 4000);
                        }
                    showToastMessage("Auto Update: ON");
                } else {
                    autoUpdate = false;
                }
                    mainButtonUpdate.setEnabled(true);
                    mainButtonUpdate.setBackgroundResource(R.drawable.selector);
                    showToastMessage("Auto Update: OFF");
                }
        });

        //Exploration
        startToggleButtonExplore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread, 0);
                    sendMessage(preferred.getString("ExploreCommand", ""));
                } else {
                    timeSwapBuff += timeInMilliseconds;
                    customHandler.removeCallbacks(updateTimerThread);

                }
            }
        });

        // Fastest Path
        startToggleButtonFastest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startTime1 = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread1, 0);
                    sendMessage(preferred.getString("FastestCommand", ""));
                } else {
                    timeSwapBuff1 += timeInMilliseconds1;
                    customHandler.removeCallbacks(updateTimerThread1);
                }
            }
        });


        RHSButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (getLastOpenedTab() > 1) {
                    swapTab(1);
                }
            }
        });
        RHSButtonAMD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                tabManager(2);
            }
        });
        RHSButtonMDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                tabManager(3);
            }
        });
        RHSButtonCoord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                tabManager(4);
            }
        });
        RHSButtonFeatures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                tabManager(5);
            }
        });
        RHSButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {tabManager(6);
            }
        });


        coordButtonWpSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setWaypoint();
            }
        });
        coordButtonWpReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                clearWaypoint();
            }
        });
        coordButtonRobotSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setRobot();
            }
        });
        coordButtonRobotReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetAll();
            }
        });

        coordSpinnerRobotDirection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch(position) {
                    case 0:
                        coordImageViewRobot.setImageDrawable(getResources().getDrawable(R.drawable.coord_robot_0));
                        break;
                    case 1:
                        coordImageViewRobot.setImageDrawable(getResources().getDrawable(R.drawable.coord_robot_90));
                        break;
                    case 2:
                        coordImageViewRobot.setImageDrawable(getResources().getDrawable(R.drawable.coord_robot_180));
                        break;
                    case 3:
                        coordImageViewRobot.setImageDrawable(getResources().getDrawable(R.drawable.coord_robot_270));
                        break;
                    default:
                        coordImageViewRobot.setImageDrawable(getResources().getDrawable(R.drawable.coord_robot_0));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        coordImageViewRobot.setOnDragListener(new dragListenerRobot());
        mapDisplay.setOnDragListener(new dragListenerMap());

        coordImageViewRobot.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(coordImageViewRobot);
                v.startDrag(data, shadow, null, 0);
                return true;
            }
        });

        //Tilt Switch-------------------------------------------------------------------
        featuresSwitchTilt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tiltMode = true;
                    showToastMessage("Tilt On");
                } else {
                    tiltMode = false;
                    showToastMessage("Tilt Off");
                }
            }
        });
        //Voice Button-----------------------------------------------------------------------------
        featuresButtonVoice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displaySpeechRecognizer();
            }
        });
        featuresButtonGoToCommands.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                featuresGoToCommands();
            }
        });

        startButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetAll();
            }
        });

    }

    private class dragListenerRobot implements View.OnDragListener {
        public boolean onDrag(View v, DragEvent event) {
                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            break;
                        case DragEvent.ACTION_DRAG_ENTERED:
                            break;
                        case DragEvent.ACTION_DRAG_LOCATION:
                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                            //when robot is dragged
                            robotDragged = true;
                            return true;
                        case DragEvent.ACTION_DROP:
                            break;
                        case DragEvent.ACTION_DRAG_ENDED:
                            break;
                        default:
                            return false;
            }
            return true;
        }
    }
    private class dragListenerMap implements View.OnDragListener {
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();

            switch(action){
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    //v.get(X) float -> Math.round int
                    int rowInt = Integer.valueOf(Math.round(event.getX()));
                    int columnInt = Integer.valueOf(Math.round(event.getY()));

                    intialiseRowColumn(rowInt,columnInt);
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    String newPosition = "{go:[";

                    String column = coordTextViewRobotColumn.getText().toString();
                    String row = coordTextViewRobotRow.getText().toString();

                    newPosition += coordTextViewRobotColumn.getText().toString() + ", ";
                    newPosition += coordTextViewRobotRow.getText().toString() + ", ";
                    newPosition += coordSpinnerRobotDirection.getSelectedItem().toString() + "]}";

                    if (column.contentEquals(getString(R.string.coordinates_out_of_bounds)) || row.contentEquals(getString(R.string.coordinates_out_of_bounds))) {
                        showToastMessage("Cannot set coordinates! Please stay within the map.");
                    } else {
                        newPosition += column + ", ";
                        newPosition += row + ", ";
                        newPosition += coordSpinnerRobotDirection.getSelectedItem().toString() + "]}";

                        try {
                            decodedString = decodeRobotString(newPosition);
                            updateGridArray(toIntArray(decodedString));
                            showToastMessage("Position Recorded");
                        } catch (JSONException e) {
                            showToastMessage("Failed to update");
                            e.printStackTrace();
                        }
                    }
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
                default:
                    showToastMessage("Unknown drop type");
            }
            return false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // If Bluetooth is not on, request that it be enabled. //
        // setupChat() will then be called during onActivityResult //
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null)
                setupChat();
        }

    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        // Resume the Bluetooth when it first fail onStart() //
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    //map starts at top right (20,20) because of black border
    private void intialiseRowColumn(int row, int column) {
        //row (horizontal axis) is from 675 to 20 (20 squares)
        int rowStart = 675;
        int rowEnd = 20;
        //x grid height
        int rowGridWidth = 33;

        //column (horizontal axis) is from 20 to 510 (15 squares)
        int columnStart = 20;
        int columnEnd = 510;
        //y grid width
        int columnGridHeight = 33;

        boolean error = false;

        //error checking (out of bounds)
        if (row < rowEnd || row > rowStart) {
            displayCoordError(1);
            error = true;
        }

        if (column < columnStart || column > columnEnd) {
            displayCoordError(2);
            error = true;
        }

        if(!error) {
            calculateRow(row, rowStart, rowGridWidth);
            calculateColumn(column, columnStart, columnGridHeight);
        }
    }
    private void calculateRow(int row, int rowStart, int length) {
        int grid = ((rowStart - row) / length);
        setRow(grid);
    }
    private void calculateColumn(int column, int columnStart, int length) {
        int grid = ((column - columnStart) / length);
        setColumn(grid);
    }
    private void setRow(int row) {
        if (robotDragged) {
            coordTextViewRobotRow.setText(String.valueOf(row));
        } else {
            coordSpinnerWpRow.setSelection(row);
        }
    }
    private void setColumn(int column) {
        if (robotDragged) {
            coordTextViewRobotColumn.setText(String.valueOf(column));
        } else {
            coordSpinnerWpColumn.setSelection(column);
        }
    }
    private void displayCoordError(int error) {
        switch (error) {
            case 1:
                coordTextViewRobotRow.setText(getString(R.string.coordinates_out_of_bounds));
                break;
            case 2:
                coordTextViewRobotColumn.setText(getString(R.string.coordinates_out_of_bounds));
                break;

        }
    }

    //RHS Menu Management
    private void tabManager(int tab) {
        if (getLastOpenedTab() == 0) {
            openTab(tab);

            RHSButtonMain.setBackgroundColor(getResources().getColor(R.color.grey_200));
            RHSButtonMain.setTextColor(getResources().getColor(R.color.blue_grey_800));
        } else if(getLastOpenedTab() == tab) {
            closeTab();
            lastOpenedTab = 0;

            RHSButtonMain.setBackgroundColor(getResources().getColor(R.color.blue_grey_800));
            RHSButtonMain.setTextColor(getResources().getColor(R.color.grey_200));
        } else {
            swapTab(tab);
        }
    }

    //only need to add your new tab and button to getLinearLayout and getButton respectively
    private LinearLayout getLinearLayout(int tab) {
        switch(tab) {
            case 1:
                return mainLayoutMain;
            case 2:
                return mainLayoutAMD;
            case 3:
                return mainLayoutMDF;
            case 4:
                return mainLayoutCoord;
            case 5:
                return mainLayoutFeatures;
            case 6:
                return mainLayoutStart;
            default:
                return mainLayoutMain;
        }
    }
    private Button getTabButton(int tab) {
        switch(tab) {
            case 1:
                return RHSButtonMain;
            case 2:
                return RHSButtonAMD;
            case 3:
                return RHSButtonMDF;
            case 4:
                return RHSButtonCoord;
            case 5:
                return RHSButtonFeatures;
            case 6:
                return RHSButtonStart;
            default:
                return RHSButtonMain;
        }
    }
    private int getLastOpenedTab() {
        return lastOpenedTab;
    }

    private void openTab(int tab) {
        LinearLayout layout = getLinearLayout(tab);
        Button button = getTabButton(tab);

        mainLayoutMain.setVisibility(View.VISIBLE);
        layout.startAnimation(animSlideUp);
        layout.setVisibility(View.VISIBLE);
        layout.setClickable(true);
        lastOpenedTab = tab;

        button.setBackgroundColor(getResources().getColor(R.color.blue_grey_800));
        button.setTextColor(getResources().getColor(R.color.grey_200));
    }
    private void closeTab() {
        LinearLayout layout = getLinearLayout(lastOpenedTab);
        Button button = getTabButton(lastOpenedTab);

        mainLayoutMain.setVisibility(View.VISIBLE);
        layout.startAnimation(animSlideDown);
        layout.setVisibility(View.INVISIBLE);
        layout.setClickable(false);

        button.setBackgroundColor(getResources().getColor(R.color.grey_200));
        button.setTextColor(getResources().getColor(R.color.blue_grey_800));
    }
    private void swapTab(int tab) {
        closeTab();
        openTab(tab);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    //Detects clicks outside frame layout
    public boolean onTouchEvent(MotionEvent e) {
        if (lastOpenedTab != 0) {
            hideKeyboard();
            return true;
        }
        else
            return true;
    }

    // Timer function for Exploration---------------------------------------------------------------
    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            int milli = milliseconds / 10;
            if (mins < 10) {
                startTextViewExploreTime.setText("0" + mins + ":"
                        + String.format("%02d", secs) + ":"
                        + String.format("%02d", milli));
            } else {
                startTextViewExploreTime.setText(mins + ":"
                        + String.format("%02d", secs) + ":"
                        + String.format("%02d", milli));
            }
            customHandler.postDelayed(this, 0);

        }

    };

    // Timer function for startToggleButtonFastest-------------------------------------------------------------------
    private Runnable updateTimerThread1 = new Runnable() {

        public void run() {

            timeInMilliseconds1 = SystemClock.uptimeMillis() - startTime1;

            updatedTime1 = timeSwapBuff1 + timeInMilliseconds1;

            int secs = (int) (updatedTime1 / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime1 % 1000);
            int milli = milliseconds / 10;
            if (mins < 10) {
                startTextViewFastestTime.setText("0" + mins + ":"
                        + String.format("%02d", secs) + ":"
                        + String.format("%02d", milli));
            } else {
                startTextViewFastestTime.setText(mins + ":"
                        + String.format("%02d", secs) + ":"
                        + String.format("%02d", milli));
            }
            customHandler.postDelayed(this, 0);
        }

    };


    // MAP INITIALIZATION //------------------------------------------------------------------------
    private void init() {
        // initialize the default robot position, grid map size //
        gridString = "GRID 15 20 3 3 2 3 0 0 0 0 0 0 0";
        coordTextViewRobotRow.setText("18");
        coordTextViewRobotColumn.setText("1");
        coordSpinnerRobotDirection.setSelection(1);
        coordSpinnerWpRow.setSelection(0);
        coordSpinnerWpColumn.setSelection(0);

        intArray = toIntArray(gridString);
        Arena = new Map(this, intArray);
        Arena.setClickable(false);
        Arena.setGridArray(intArray);
        Arena.setObstacleArray(obstacleSensor);
        Arena.setUnExploredArray(unexploredSensor);
        Arena.setExploredArray(exploredSensor);
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 15; x++) {
                obsArray[x][y] = "0";
            }
        }
        mapDisplay = (RelativeLayout) findViewById(R.id.mapView); //mapView is under activity_main.xml
        mapDisplay.addView(Arena);
    }

    //Bluetooth Chat SetUp-------------------------------------------------------------------------------
    private void setupChat() {
        // Initialize the array adapter for the conversation thread //
        amdArrayAdapterSend = new ArrayAdapter<>(this,
                R.layout.amd_arrayadapter_send);
        amdArrayAdapterReceive = new ArrayAdapter<>(this,
                R.layout.amd_arrayadapter_send);

        amdListViewSend = (ListView) findViewById(R.id.amd_listview_send);
        amdListViewSend.setAdapter(amdArrayAdapterSend);

        amdListViewReceive = (ListView) findViewById(R.id.amd_listview_receive);
        amdListViewReceive.setAdapter(amdArrayAdapterReceive);

        amdSendButton = (Button) findViewById(R.id.amd_send_button);
        amdSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText TextAMD = (EditText) findViewById(R.id.amd_edittext_send_text);
                String message = TextAMD.getText().toString();
                sendMessage(message);
                TextAMD.setText("");
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections //
        mChatService = new BluetoothService(this, mHandler);

        // Initialize the buffer for outgoing messages //
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services //
        if (mChatService != null)
            mChatService.stop();
    }

    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there is actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            // FOR AMD HERE //
            byte[] send = message.getBytes();
            mChatService.write(send);
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
    }

    // The action listener for the EditText widget, to listen for the return key //
    private TextView.OnEditorActionListener amdListenerSend = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId,
                                      KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL
                    && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    //AppBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(getApplication()).inflate(R.menu.options_menu, menu);
        return true;
    }
    private void setStatus(int resId) {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setSubtitle(resId);
    }
    private void setStatus(CharSequence subTitle) {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    //---------------------------------------------------------------------------------------------------------------------------
    // The Handler that gets information back from the BluetoothChatService // handler here //
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to) + " " + mConnectedDeviceName);
                            amdArrayAdapterSend.clear();

                            break;
                        case BluetoothService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //show msg in Interaction Tab (Tab2)
                    amdArrayAdapterSend.add("Group 8:  " + writeMessage);
                    break;

                //reading whenever a object is send to us
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    amdArrayAdapterReceive.add(mConnectedDeviceName + ": " + readMessage);

                    if (readMessage.contains("x")) {
                        if (readMessage.startsWith("w")) {
                            straight();
                            mainTextViewRobotStatus.setText(getString(R.string.robot_status_forward));
                        } else if (readMessage.startsWith("a")) {
                            turningLeft();
                            mainTextViewRobotStatus.setText(getString(R.string.robot_status_turn_left));
                        } else if (readMessage.startsWith("d")) {
                            turningRight();
                            mainTextViewRobotStatus.setText(getString(R.string.robot_status_turn_right));
                        } else if (readMessage.startsWith("s")) {
                            turningRight();
                            turningRight();
                            mainTextViewRobotStatus.setText(getString(R.string.robot_status_reverse));
                        }

                        String[] messageSplit = readMessage.split("//");

                        String explored = messageSplit[1];
                        String Obstacle = messageSplit[2];

                        ArrayList mdf1BINArray = MDFConverter.hexToBin2(explored);
                        ArrayList mdf2BINArray = MDFConverter.hexToBin2(Obstacle);

                        ArrayList<Integer> eArray = new ArrayList<>();
                        ArrayList<Integer> oArray = new ArrayList<>();

                        int j = 0;
                        for (int k = 2; k < (mdf1BINArray.size() - 2); k++) {
                            int temp1 = (int) mdf1BINArray.get(k);
                            if (temp1 == 1) {
                                if (k - 3 < 0) {
                                    eArray.add(299);
                                } else {
                                    eArray.add(k - 3);
                                }

                                int temp2 = (int) mdf2BINArray.get(j);
                                if (temp2 == 1) {
                                    if (j < (mdf2BINArray.size())) {
                                        if (k - 3 < 0) {
                                            oArray.add(299);
                                        } else {
                                            oArray.add(k - 3);
                                        }
                                        j++;
                                    }
                                } else {
                                    if (j < (mdf2BINArray.size())) {
                                        j++;
                                    }
                                }
                            }
                        }
                        eArray = flipArrayList(eArray);

                        mdfTextViewExplored.setText(Obstacle.toString());
                        mdfTextViewExploredObstacle.setText(explored);

                        if (mainSwitchAutoManual.isChecked()) {
                            updateExploredArray(eArray);
                            updateObstacleArray(oArray);
                        }
                    }else if (readMessage.contains("w")) {
                        if (readMessage.length() > 1)
                            ArrayPath(readMessage);
                        else {
                            straight();
                            mainTextViewRobotStatus.setText(getString(R.string.robot_status_forward));
                        }
                    } else if (readMessage.contains("a")) {
                        if (readMessage.length() > 1)
                            ArrayPath(readMessage);
                        else {
                            turningLeft();
                            mainTextViewRobotStatus.setText(getString(R.string.robot_status_turn_left));
                        }
                    } else if (readMessage.contains("d")) {
                        if (readMessage.length() > 1)
                            ArrayPath(readMessage);
                        else {
                            turningRight();
                            mainTextViewRobotStatus.setText(getString(R.string.robot_status_turn_right));
                        }
                    } else if (readMessage.contains("s")) {
                        if (readMessage.length() > 1)
                            ArrayPath(readMessage);
                        else {
                            turningRight();
                            turningRight();
                            mainTextViewRobotStatus.setText(getString(R.string.robot_status_reverse));
                        }
                    }
                    break;

                case MESSAGE_DEVICE_NAME:
                    // save the connected device name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(),
                            "Connected to " + mConnectedDeviceName,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    };

    private void ArrayPath(String message) {
         ArrayList<String> pathArray = new ArrayList();

        for (int i=0; i< message.length(); i++) {
            pathArray.add(i,Character.toString(message.charAt(i)));
        }
        for (int i=0; i< pathArray.size(); i++) {
            final String movement = pathArray.get(i);
            if (movement.startsWith("w")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        straight();
                        mainTextViewRobotStatus.setText(getString(R.string.robot_status_forward));
                    }
                }, 2000);

            } else if (movement.startsWith("a")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        turningLeft();
                        mainTextViewRobotStatus.setText(getString(R.string.robot_status_turn_left));
                    }
                }, 2000);
            } else if (movement.startsWith("d")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        turningRight();
                        mainTextViewRobotStatus.setText(getString(R.string.robot_status_turn_right));
                    }
                }, 2000);
            } else if (movement.startsWith("s")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        turningRight();
                        turningRight();
                        mainTextViewRobotStatus.setText(getString(R.string.robot_status_reverse));
                    }
                }, 2000);
            }

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case SPEECH_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // Fill the list view with the strings the recognizer thought it could have heard
                    List<String> results = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);

                    spokenText = results.get(0);
                    featuresTextViewSpokenText.setText(spokenText);

                    if (spokenText.contentEquals(preferred.getString("DownCommandVoice",""))) {
                        turningRight();
                        turningRight();
                        showToastMessage(getString(R.string.robot_status_reverse));
                    } else if (spokenText.contentEquals(preferred.getString("UpCommandVoice",""))) {
                        straight();
                        showToastMessage(getString(R.string.robot_status_forward));
                    } else if (spokenText.contentEquals(preferred.getString("RightCommandVoice",""))) {
                        turningRight();
                        showToastMessage(getString(R.string.robot_status_turn_right));
                    } else if (spokenText.contentEquals(preferred.getString("LeftCommandVoice",""))) {
                        turningLeft();
                        showToastMessage(getString(R.string.robot_status_turn_left));
                    } else if (spokenText.contentEquals(preferred.getString("ExploreCommandVoice",""))) {
                        sendMessage(preferred.getString("ExploreCommand",""));
                        showToastMessage(getString(R.string.robot_status_exploring));
                    } else if (spokenText.contentEquals(preferred.getString("FastestCommandVoice",""))) {
                        sendMessage(preferred.getString("FastestCommand",""));
                        showToastMessage(getString(R.string.robot_status_fastest_path));
                    } else {
                        showToastMessage("Error! Command not recognised");
                    }
                }
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras().getString(
                DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, true);
    }

    private int[] toIntArray(String s) {
        String[] stringArray = s.split(" ");
        int length = stringArray.length - 1;
        int[] intArray = new int[length];

        for (int i = 1; i <= length; i++) {
            intArray[i - 1] = Integer.parseInt(stringArray[i]);
        }

        int fx = intArray[2];
        int fy = intArray[3];
        int bx = intArray[4];
        int by = intArray[5];

        intArray[2] = fx;
        intArray[3] = fy;
        intArray[4] = bx;
        intArray[5] = by;
        int i;

        return intArray;
    }

    private void updateObstacleArray(ArrayList tempObstacleArray) {
        if (autoUpdate) {
            Arena.setObstacleArray(tempObstacleArray);
        }
    }
    private void updateExploredArray(ArrayList tempExploredArray) {
        if (autoUpdate) {
            Arena.setExploredArray(tempExploredArray);
        }
    }

    //auto update on robot
    private void updateGridArray(int[] tempGridArray) {
        if (autoUpdate) {
            Arena.setGridArray(tempGridArray);
        }
    }

    private String decodeRobotStringFromRobot(String message) throws JSONException {
        jsonObject = new JSONObject(message);
        String decodedMessage = jsonObject.getString("go");

        //remove unnecessary symbols
        decodedMessage = decodedMessage.replace("[", "");
        decodedMessage = decodedMessage.replace("]", "");

        Integer robot_X = xStatus;
        Integer robot_Y = yStatus;
        Integer robot_D = dStatus;

        //forwarding
        if (decodedMessage.equals("\"X\"")) {
            //Log.d("YES: ","Yes!");
            switch (dStatus) {
                case 0:
                    robot_Y = yStatus - 1;
                    break;
                case 90:
                    robot_X = xStatus + 1;
                    break;
                case 180:
                    robot_Y = yStatus + 1;
                    break;
                case 270:
                    robot_X = xStatus - 1;
            }
        }

        //turning right
        if (decodedMessage.equals("\"R\"")) {
            switch (dStatus) {
                case 0:
                    robot_D = 90;
                    break;
                case 90:
                    robot_D = 180;
                    break;
                case 180:
                    robot_D = 270;
                    break;
                case 270:
                    robot_D = 0;
            }
        }
        // turning left
        if (decodedMessage.equals("\"L\"")) {
            switch (dStatus) {
                case 0:
                    robot_D = 270;
                    break;
                case 90:
                    robot_D = 0;
                    break;
                case 180:
                    robot_D = 90;
                    break;
                case 270:
                    robot_D = 180;
            }
        }
        return decodeRobotString_(robot_X, robot_Y, robot_D);
    }

    private ArrayList flipArrayList(ArrayList arrayList) {
        int tempInt;
        int correctExplored;
        int remaining;
        int count=0;
        int correctRow;

        int IntInArray = 0;
        ArrayList tempArrayList = new ArrayList();
        int i = arrayList.size()-1;

        while (i != 0) {
            tempInt = Integer.parseInt(arrayList.get(i).toString());
            correctExplored = 298 - tempInt;
            if (correctExplored < 15) {
                IntInArray = 14 - correctExplored;
            } else {
                remaining = correctExplored - 14;
                while (remaining > 15) {
                    remaining = remaining - 15;
                    count++;
                }
                correctRow = 15 - (correctExplored - (count * 15));
                IntInArray = ((count + 2) * 15) + (correctRow - 1);
            }
            count = 0;
            tempArrayList.add(IntInArray);
            i--;
        }

        return tempArrayList;
    }

    // Decode the String from the AMD in json format
    private String decodeRobotString(String message) throws JSONException {

        jsonObject = new JSONObject(message);
        String decodedMessage = jsonObject.getString("go");

        // remove unnecessary symbols
        decodedMessage = decodedMessage.replace("[", "");
        decodedMessage = decodedMessage.replace("]", "");
        String stringArray[] = decodedMessage.split(",");

        // Decode the Strings and assign to individual axis
        Integer robot_X = Integer.parseInt(stringArray[0]);
        Integer robot_Y = Integer.parseInt(stringArray[1]);
        Integer robot_D = Integer.parseInt(stringArray[2]);

        return decodeRobotString_(robot_X, robot_Y, robot_D);
    }
    private String decodeRobotString_(Integer robot_X, Integer robot_Y, Integer robot_D) {
        String decodedMessage = "";
        String fx = "";
        String fy = "";
        String bx = "";
        String by = "";

        // mainButtonUpdate Spinner Value X & Y according to latest Coordinates
        coordTextViewRobotColumn.setText(robot_X.toString());
        coordTextViewRobotRow.setText(robot_Y.toString());

        if (robot_D == 0) {
            fx = String.valueOf(20 - robot_Y);
            fy = String.valueOf(robot_X + 3);
            bx = String.valueOf(20 - robot_Y - 1);
            by = String.valueOf(robot_X + 3);

            // mainButtonUpdate Spinner Value Angle according to latest Coordinates
            coordSpinnerRobotDirection.setSelection(0);

            // Display the robot direction base on previous direction which is dStatus
            if (dStatus == 270) {
                mainTextViewRobotStatus.setText(getString(R.string.robot_status_turn_right));
            }
            if (dStatus == 90) {
                mainTextViewRobotStatus.setText(getString(R.string.robot_status_turn_left));
            }

            // Base on location and direction, decode the robot movement. (Straight or Reverse)
            if (robot_Y < yStatus) {
                mainTextViewRobotStatus.setText(getString(R.string.robot_status_forward));
            }
            if (robot_Y > yStatus) {
                mainTextViewRobotStatus.setText(getString(R.string.robot_status_reverse));
            }
        }

        // Repeat the method for angle, 90, 180 & 270 //
        else if (robot_D == 90) {
            fx = String.valueOf(20 - robot_Y);
            fy = String.valueOf(robot_X + 3);
            bx = String.valueOf(20 - robot_Y);
            by = String.valueOf(robot_X + 3);

            coordSpinnerRobotDirection.setSelection(1);

            if (dStatus == 0) {
                mainTextViewRobotStatus.setText(getString(R.string.robot_status_turn_right));
            }
            if (dStatus == 180) {
                mainTextViewRobotStatus.setText(getString(R.string.robot_status_turn_left));
            }
            if (robot_X > xStatus) {
                mainTextViewRobotStatus.setText(getString(R.string.robot_status_forward));
            }
            if (robot_X < xStatus) {
                mainTextViewRobotStatus.setText(getString(R.string.robot_status_reverse));
            }

        } else if (robot_D == 180) {
            fx = String.valueOf(20 - robot_Y - 2);
            fy = String.valueOf(robot_X + 3);
            bx = String.valueOf(20 - robot_Y - 1);
            by = String.valueOf(robot_X + 3);

            coordSpinnerRobotDirection.setSelection(2);

            if (dStatus == 90) {
                mainTextViewRobotStatus.setText(getString(R.string.robot_status_turn_right));
            }
            if (dStatus == 270) {
                mainTextViewRobotStatus.setText(getString(R.string.robot_status_turn_left));
            }
            if (robot_Y > yStatus) {
                mainTextViewRobotStatus.setText(getString(R.string.robot_status_forward));
            }
            if (robot_Y < yStatus) {
                mainTextViewRobotStatus.setText(getString(R.string.robot_status_reverse));
            }

        } else if (robot_D == 270) {
            fx = String.valueOf(20 - robot_Y);
            fy = String.valueOf(robot_X + 1);
            bx = String.valueOf(20 - robot_Y);
            by = String.valueOf(robot_X + 3);

            coordSpinnerRobotDirection.setSelection(3);

            if (dStatus == 180) {
                mainTextViewRobotStatus.setText(getString(R.string.robot_status_turn_right));
            }
            if (dStatus == 0) {
                mainTextViewRobotStatus.setText(getString(R.string.robot_status_turn_left));
            }
            if (robot_X < xStatus) {
                mainTextViewRobotStatus.setText(getString(R.string.robot_status_forward));
            }
            if (robot_X > xStatus) {
                mainTextViewRobotStatus.setText(getString(R.string.robot_status_reverse));
            }

        }

        myHandler.postDelayed(mMyRunnable, 1000); // Delay the mainTextViewRobotStatus by 1 sec (Visibility)
        // Combine Strings into Grid Format
        decodedMessage = "GRID 15 20 " + fx + " " + fy + " " + bx + " " + by + " 0 0 0 0 0 0 0 0";

        // update the previous coordinates to the newly ones.
        xStatus = robot_X;
        yStatus = robot_Y;
        dStatus = robot_D;

        return decodedMessage;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent Intent;
        switch (item.getItemId()) {
            case R.id.connect_devices:
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnOn, 0);
                    Toast.makeText(getApplicationContext(), "Bluetooth Enable"
                            , Toast.LENGTH_LONG).show();
                } else {
                    // Launch the DeviceListActivity to see devices and do scan
                    Intent = new Intent(this, DeviceListActivity.class);
                    startActivityForResult(Intent, REQUEST_CONNECT_DEVICE);
                }
                return true;
            case R.id.my_setting:
                Intent = new Intent(this, SettingActivity.class);
                startActivity(Intent);
                return true;
            case R.id.exit:
                mBluetoothAdapter.disable();
                System.exit(0);
                Toast.makeText(getApplicationContext(), "Bluetooth Disable", Toast.LENGTH_LONG).show();
                return true;
        }
        return false;
    }

    //reduce the delay for toast message method
    private void showToastMessage(String text) {
        final Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 500);
    }

    private void resetAll() {
        obstacleSensor = new ArrayList();
        Arena.setObstacleArray(obstacleSensor);
        Arena.setExploredArray(obstacleSensor);

        coordTextViewRobotRow.setText("16");
        coordTextViewRobotColumn.setText("0");
        coordSpinnerWpRow.setSelection(0);
        coordSpinnerWpColumn.setSelection(0);
        coordSpinnerRobotDirection.setSelection(0);

        amdArrayAdapterSend.clear();
        amdArrayAdapterReceive.clear();
        xStatus = 0;
        yStatus = 17;
        dStatus = 180;

        setRobot();
        init();

        startTime = 0L;
        startTime1 = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;
        timeInMilliseconds1 = 0L;
        timeSwapBuff1 = 0L;
        updatedTime1 = 0L;
        startTextViewExploreTime.setText(R.string.start_timer);
        startTextViewFastestTime.setText(R.string.start_timer);
        startToggleButtonExplore.setChecked(false);
        startToggleButtonFastest.setChecked(false);
    }

    private void setWaypoint() {
        if (!waypointArray.isEmpty()) {
            waypointArray.clear();
        }

        String row = coordSpinnerWpRow.getSelectedItem().toString();
        String column = coordSpinnerWpColumn.getSelectedItem().toString();
        String position = "";

        int waypointRow = Integer.valueOf(row) + 1;
        int waypointColumn = Integer.valueOf(column) + 1;

        waypointArray.add(waypointRow);
        waypointArray.add(waypointColumn);

        Arena.setWaypointArray(waypointArray);

        position += 19 - Integer.valueOf(row);
        position += "," + column + "algo}";

        sendMessage(position);
        showToastMessage("Waypoint "+ position + "and Sent");
    }
    private void clearWaypoint() {
        waypointArray.clear();
        coordSpinnerWpRow.setSelection(0);
        coordSpinnerWpColumn.setSelection(0);
    }

    private void setRobot() {
        String newPosition = "{go:[";

        String column = coordTextViewRobotColumn.getText().toString();
        String row = coordTextViewRobotRow.getText().toString();

        newPosition += coordTextViewRobotColumn.getText().toString() + ", ";
        newPosition += coordTextViewRobotRow.getText().toString() + ", ";
        newPosition += coordSpinnerRobotDirection.getSelectedItem().toString() + "]}";

        if (column.contentEquals(getString(R.string.coordinates_out_of_bounds)) || row.contentEquals(getString(R.string.coordinates_out_of_bounds))) {
            showToastMessage("Cannot set coordinates! Please stay within the map.");
        } else {
            newPosition += column + ", ";
            newPosition += row + ", ";
            newPosition += coordSpinnerRobotDirection.getSelectedItem().toString() + "]}";

            try {
                decodedString = decodeRobotString(newPosition);
                updateGridArray(toIntArray(decodedString));
                showToastMessage("Coordinates sent!");
            } catch (JSONException e) {
                showToastMessage("Failed to update");
                e.printStackTrace();
            }

            // send amd the grid string
            //sendMessage("robotPosition:" + newPosition);

            myHandler.postDelayed(mMyRunnable, 1000);
        }
    }

    private void straight() {
        mainTextViewRobotStatus.setText(R.string.robot_status_forward);

        // Updating the new robot position on Tablet
        try {
            decodedString = decodeRobotStringFromRobot("{go:[X]}");
            updateGridArray(toIntArray(decodedString));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        myHandler.postDelayed(mMyRunnable, 1000);

    }
    private void turningLeft() {
        mainTextViewRobotStatus.setText(R.string.robot_status_turn_left);
        // Updating the new robot position on Tablet
        try {
            decodedString = decodeRobotStringFromRobot("{go:[L]}");
            updateGridArray(toIntArray(decodedString));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // FOR Sending string to AMD HERE
        myHandler.postDelayed(mMyRunnable, 1000);
    }
    private void turningRight() {
        mainTextViewRobotStatus.setText(R.string.robot_status_turn_right);
        // Updating the new robot position on Tablet
        try {
            decodedString = decodeRobotStringFromRobot("{go:[R]}");
            updateGridArray(toIntArray(decodedString));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        myHandler.postDelayed(mMyRunnable, 1000);
    }

    //Here's a runnable/handler combo
    private Runnable mMyRunnable = new Runnable() {
        @Override
        public void run() {
            mainTextViewRobotStatus.setText(R.string.robot_status_stop);
        }
    };

    @Override
    //tilt feature---------------------------------------------------------------------------
    public void onSensorChanged(SensorEvent event) {
        // Check sensor type
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Assign directions
            float x = event.values[0];
            float y = event.values[1];
            long curTime = System.currentTimeMillis();

            String status = "";

            if (tiltMode) {
                //after 2000ms (2s), read data again
                if (curTime - lastUpdateTime > 2000) {
                    if (y > 4) {
                        // If y is more than 3 of first held offset, it is expected to reverse.
                        pre_state = 0;
                        String message = "hb";
                        if (bluetoothService != null) {
                            bluetoothService.write(message.getBytes());
                        }
                        if (autoUpdate) {
                            tiltReverse();
                            status = getString(R.string.robot_status_reverse);
                            showToastMessage(getString(R.string.robot_status_reverse));
                            sendMessage(preferred.getString("DownCommand", ""));
                        }
                    } else if (y < -2) {
                        // Robots moves at a very slight of tilting forward.
                        pre_state = 0;
                        String message = "hf";
                        if (bluetoothService != null) {
                            bluetoothService.write(message.getBytes());
                        }
                        if (autoUpdate) {
                            tiltStraight();
                            status = getString(R.string.robot_status_forward);
                            showToastMessage(getString(R.string.robot_status_forward));
                            sendMessage(preferred.getString("UpCommand",""));

                        }
                    }
                    lastUpdateTime = curTime;
                }

                if (x < -1) {
                        if (x > -4) {
                            pre_state = 0; // Register position as centered.

                        } else if (x <= -4) {
                            if (pre_state != 2) {
                                // Tilt right if previously the state is not tilted towards right.
                                pre_state = 2;
                                tiltTurnRight();
                                status = getString(R.string.robot_status_turn_right);
                                showToastMessage(getString(R.string.robot_status_turn_right));
                                sendMessage(preferred.getString("RightCommand",""));
                            }
                        }
                    } else if (x > 1) {
                        if (x < 4) {
                            pre_state = 0;
                        } else if (x >= 4) {
                            if (pre_state != 1) {
                                // Tilt left if previously the state is not tilted towards left.
                                pre_state = 1;
                                tiltTurnLeft();
                                status = getString(R.string.robot_status_turn_left);
                                showToastMessage(getString(R.string.robot_status_turn_left));
                                sendMessage(preferred.getString("LeftCommand",""));
                            }
                        }
                    }
                }
            if (status.length() > 0) {
                mainTextViewRobotStatus.setText(status);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    private void tiltTurnRight() {
        String message = preferred.getString("RightCommand", "");
        if (bluetoothService != null) {
            bluetoothService.write(message.getBytes());
        }
        if (autoUpdate) {
            turningRight();
        }
    }
    private void tiltTurnLeft() {
        String message = preferred.getString("LeftCommand", "");
        if (bluetoothService != null) {
            bluetoothService.write(message.getBytes());
        }
        if (autoUpdate) {
            turningLeft();
        }
    }
    private void tiltStraight() {
        String message = preferred.getString("UpCommand", "");
        if (bluetoothService != null) {
            bluetoothService.write(message.getBytes());
        }
        if (autoUpdate) {
            straight();
        }
    }
    private void tiltReverse() {
        String message = preferred.getString("DownCommand", "");
        if (bluetoothService != null) {
            bluetoothService.write(message.getBytes());
        }
        if (autoUpdate) {
            turningRight();
            turningRight();
        }
    }

    private void featuresGoToCommands() {
        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
        startActivity(intent);
    }

    //VOICE FEATURE
    // Create an intent that can start the Speech Recognizer activity // voice command activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //uses free form text input
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }
}