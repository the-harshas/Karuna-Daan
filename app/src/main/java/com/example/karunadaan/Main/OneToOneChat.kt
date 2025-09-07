package com.example.karunadaan.Main
import android.Manifest
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import android.content.Context
import com.example.karunadaan.R
import com.example.karunadaan.adapter.MessagAdapter
import com.example.karunadaan.entity.Message


class OneToOneChat : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var messageAdapter: MessagAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDatabseRef: DatabaseReference
    private lateinit var mFirebase: FirebaseFirestore
    private var MICROPHONE_PERMISSION_CODE = 200
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var mStorage: StorageReference
    private var MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 200
    val TAG = "chatActivity"
    private var audioFilePath = "";
    private lateinit var mediaPlayer: MediaPlayer
    private  lateinit var recordingImageIcon : ImageView
    private  var CONST_PALYING_AUDIO = 0
    //private  lateinit var toolbar: Toolbar
    private  lateinit var chatUserName:TextView
    private lateinit var onlineStatus:TextView

    var receiverRoom: String? = null
    var senderRoom: String? = null
    private var current_Audio_State = 1;

    // Requesting permission to RECORD_AUDIO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat2)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDatabseRef = FirebaseDatabase.getInstance().getReference()
        mFirebase = Firebase.firestore
        mProgressBar = findViewById(R.id.progressBarSendingAudio)
        recordingImageIcon = findViewById(R.id.sentButton)
        onlineStatus=findViewById(R.id.inChatonlineStatus)

        // checking if the another person is online or not and update staus accoringly
//        showNotification("Message arrived","you got a message from Nir NGO", this@ChatActivity2)


        var onlineDb=mDatabseRef.child("onlineUser")
        onlineDb.addValueEventListener( object :ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                 if(dataSnapshot.hasChild(receiverUid.toString()))
                       {
                           onlineStatus.text="Online"
                           onlineStatus.setTextColor(Color.parseColor("#4CAF50"))
                           Log.d(TAG,"online status updated"+dataSnapshot.child(receiverUid.toString()).value+" "+receiverUid.toString())
                    }
                else{
                     onlineStatus.text="Offline"
                     onlineStatus.setTextColor(Color.parseColor("#E91E63"))
                     Log.d(TAG,"NOT UPDATED")
                 }
                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })

        //for creating child with unique id for each person's chat
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        //toolbar = findViewById(R.id.chatToolbar)
        //setSupportActionBar(toolbar);
        //supportActionBar!!.setDisplayHomeAsUpEnabled(false);
        //supportActionBar!!.setTitle("")
        //toolbar.setTitle("")

        mediaPlayer = MediaPlayer()
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        chatUserName = findViewById(R.id.donarUserName)
        chatUserName.text = name!!.capitalize()

        // setting up message Recycler view
        messageList = ArrayList()
        messageAdapter = MessagAdapter(this, messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        messageAdapter.setOnClickListener(object :
            MessagAdapter.OnClickListener{
            override fun onClick(position: Int, model: Message) {
                Log.d(TAG,"On click recycler view"+" "+model.message +" "+model.audio)
                if (model.audio == 1) {
                    Log.d(TAG, "message box clicked")
                    playAudio( model.uri!!, position )
                }
                //
            }
        }
        )
        mStorage = Firebase.storage.reference

        if(!isMicrophonePresent()){
            getMicrophonePermission()
        }
        checkFilePermission()
        messageBox.addTextChangedListener( object :TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(current_Audio_State  !=0 ) {
                    if (!messageBox.text.equals("")) {
                        current_Audio_State = 2
                        recordingImageIcon.setBackgroundResource(R.drawable.send_alt_1)
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {
                if(current_Audio_State !=0){
                    if(messageBox.text.equals("")){
                        current_Audio_State = 1
                        recordingImageIcon.setBackgroundResource(R.drawable.recording_01)
                    }
                }
            }

        })

        // update layout when keyboard opens
        val rootView = findViewById<View>(R.id.chatActivity2)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) { // if keyboard is open
                //  chatRecyclerView.scrollToPosition(messageList.size - 1)
            }
        }

        // update recycler view when keyboard opens

//        messageBox.setOnFocusChangeListener { _, hasFocus ->
//            if (hasFocus) {
//                chatRecyclerView.postDelayed({
//                    chatRecyclerView.scrollToPosition(messageList.size - 1)
//                }, 100) // Adjust delay if needed
//            }
//        }


        //logic for adding data to recycler view
        val x = mDatabseRef.child("chats").child(senderRoom!!).child("message").orderByChild("messageTime").limitToLast(20)
        x.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val newMessages = ArrayList<Message>()
                Log.d(TAG,"posSnapshot for audio search reloading list")

                for (postSnapshot in snapshot.children) {
                    val message = postSnapshot.getValue(Message::class.java)
                    if (message != null && !messageList.contains(message)) {
                        newMessages.add(message)
                    }
                    // condition below just for checking / confirmation criteria while developing ;
//                    if (message!!.audio==0) {
//
//                    }else if(message!!.audio==1){
//
//                        Log.d(TAG,"audio message added")
//                    }
                }
                messageList.addAll(newMessages)
//                chatRecyclerView.scrollToPosition(messageList.size-1)
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        sendButton.setOnClickListener {
            if(current_Audio_State == 2) {
                val message = messageBox.text.toString()
                val messageObject = Message(message, senderUid)

                mDatabseRef.child("chats").child(senderRoom!!).child("message").push()
                    .setValue(messageObject).addOnSuccessListener {
                        if(senderRoom!=receiverRoom) {
                            mDatabseRef.child("chats").child(receiverRoom!!).child("message").push()
                                .setValue(messageObject)
                        }
                    }
                messageBox.setText("")
                current_Audio_State = 1
                recordingImageIcon.setBackgroundResource(R.drawable.recording_record_sing_voice_microphone)
            }
            else if (current_Audio_State == 1) {
                current_Audio_State = 0;
                onClickRecordAudio();
                recordingImageIcon.setBackgroundResource(R.drawable.recording_01)
                Log.d(TAG,"File location after recording start "+audioFilePath);

            } else if(current_Audio_State == 0) {
                current_Audio_State = 1;
                Log.d(TAG,"file location "+audioFilePath)
                onClickStopAudio()
                recordingImageIcon.setBackgroundResource(R.drawable.recording_record_sing_voice_microphone)

            }
        }

//        scroll till required
//        chatRecyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            if (LinearLayoutManager(this).findLastCompletelyVisibleItemPosition() != messageList.size - 1)
//            {
//                loadMore()  }
//        }
        chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)

    }

    private fun loadMore() {
        val x = mDatabseRef.child("chats").child(senderRoom!!).child("message").orderByChild("messageTime").limitToLast(messageList.size+20)
        //for ()
        // val snapshot = x.values<>()
        x.addListenerForSingleValueEvent( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val message = postSnapshot.getValue(Message::class.java)

                    messageList.add(message!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        });
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.chat_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun onClickStopAudio() {
        try {
            mediaRecorder.stop()
            mediaRecorder.release()
            mProgressBar.visibility = View.VISIBLE
            recordingImageIcon.visibility = View.GONE
            uploadAudio()
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Failed to stop the media recorder properly: ${e.message}")
        } catch (e: RuntimeException) {
            Log.e(TAG, "Unexpected error while stopping the media recorder: ${e.message}")
        }
    }


    private fun uploadAudio() {

        if(audioFilePath==""){
            Log.d(TAG,"error file location")
            return
        }
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val current = formatter.format(time)

        var mStorageRef = mStorage.child("audio").child(senderRoom!!).child("message_sent").child(current.toString())
        var uri = Uri.fromFile(File(audioFilePath))
        var uploadTask = mStorageRef.putFile(uri)


        uploadTask.addOnSuccessListener {
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                mStorageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    Log.d(TAG,"dounloaduri  -- "+downloadUri)
                    var dounloadUrl = mStorageRef.downloadUrl
                    val messageObject = Message("AUDIO", FirebaseAuth.getInstance().currentUser?.uid,downloadUri.toString())
                    mDatabseRef.child("chats").child(senderRoom!!).child("message")
                        .push().setValue(messageObject).addOnCompleteListener {
                            mProgressBar.visibility = (View.GONE)
                            recordingImageIcon.visibility =(View.VISIBLE)

                        }.addOnFailureListener {
                            Log.d(TAG, "Failed to add on realtime firebase")
                        }
                    mDatabseRef.child("chats").child(receiverRoom!!).child("message")
                        .push().setValue(messageObject).addOnCompleteListener {
                            mProgressBar.visibility = (View.GONE)
                            recordingImageIcon.visibility =(View.VISIBLE)

                        }.addOnFailureListener {
                            Log.d(TAG, "Failed to add on realtime firebase")
                        }
                } else {
                    // Handle failures
                    // ...
                }
            }


        }.addOnCompleteListener {

        }.addOnFailureListener {
            messageBox.setText("Failed to upload")
            Log.d(TAG, "Filed to add on storage -- "+audioFilePath)
        }


    }

    private fun onClickRecordAudio() {

        if (isMicrophonePresent()) {
            try {
                mediaRecorder = MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                audioFilePath = getRecordingFilePath()
                mediaRecorder.setOutputFile(audioFilePath);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.prepare();
                mediaRecorder.start()
            } catch (e: Exception) {
                Log.w(TAG, "error mic permission " + e)
                e.printStackTrace();
                return;
            }
        }
        else {
            Log.d(TAG,"Per mission denied")
            getMicrophonePermission()
            current_Audio_State =1
        }

    }

    private fun isMicrophonePresent(): Boolean {
        if (this.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            return true;
        } else {
            return false;
        }
    }

    private fun getMicrophonePermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            )
            == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                MICROPHONE_PERMISSION_CODE
            );
        }
    }

    private fun getRecordingFilePath(): String {
        val musicDirectory =
            ContextWrapper(applicationContext).getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val current = formatter.format(time)
        var file = File(musicDirectory, "Dark_recording_file "+current + ".mp3");
        Log.d(TAG," absoulute path "+file.absolutePath)
        return file.absolutePath.toString()

    }

    private fun checkFilePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {  Log.d(TAG,"Permission storage denied")
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                )
            }

        }
    }

    private fun playAudio(audioUrl: String, postion:  Int) {

        if (mediaPlayer.isPlaying){

            mediaPlayer.pause()
            if( CONST_PALYING_AUDIO == postion ) {
                CONST_PALYING_AUDIO = -1 ;
                return }
        }
        CONST_PALYING_AUDIO = postion
        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        Log.d(TAG, "mediplayer is set--" + (audioUrl))
        try {
            mediaPlayer.setDataSource(audioUrl)
            // below line is use to prepare
            // and start our media player.
            mediaPlayer.prepare()
            mediaPlayer.start()
            Log.d(TAG, "Audio started playing")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // below line is use to display a toast message.
        Toast.makeText(this, "Audio started playing..", Toast.LENGTH_SHORT).show()

    }
    private fun showNotification(title: String?, message: String?, context:Context) {
        val channelId = "message_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Message Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.post_comment_icon)
            .build()

        notificationManager.notify(0, notification)
    }
}