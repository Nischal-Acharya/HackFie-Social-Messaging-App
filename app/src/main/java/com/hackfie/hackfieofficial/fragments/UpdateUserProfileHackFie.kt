import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hackfie.hackfieofficial.R
import com.hackfie.hackfieofficial.databinding.FragmentUpdateUserProfileHackFieBinding
import com.hackfie.hackfieofficial.model.utils.USERS_NODE
import com.hackfie.hackfieofficial.model.utils.USERS_PROFILE_FOLDER
import com.hackfie.hackfieofficial.model.utils.uploadImage
import java.util.Calendar

class UpdateUserProfileHackFie : Fragment(), DatePickerDialog.OnDateSetListener {


    public var profileUrl: String? = null
    var previousProfileUrl: String? = null
    var userPassword: String? = null
    private lateinit var db: FirebaseFirestore

    private var _binding: FragmentUpdateUserProfileHackFieBinding? = null
    private val binding get() = _binding!!

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        uri->
        uri?.let {

            uploadImage(uri, USERS_PROFILE_FOLDER){
                if(it==null){
                    Toast.makeText(this.context, "Error Uploading Image", Toast.LENGTH_SHORT).show()
                }else{
                    profileUrl = it
                    binding.updateUserProfileImage.setImageURI(uri)
                }
            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateUserProfileHackFieBinding.inflate(inflater, container, false)
        return binding.root
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val aahileLoginVakoUser = FirebaseAuth.getInstance().currentUser
        val userkoID = aahileLoginVakoUser?.uid.toString()
        fetchData(userkoID)


        binding.plusToUpdateProfileImage.setOnClickListener {
            launcher.launch("image/*")
        }


        // Yeha chai manxe le input field ma click gare vane error text lai clear gareko
        binding.fullNameField.setOnClickListener {
            binding.errorText.text = ""
        }
        binding.dobField.setOnClickListener {
            binding.errorText.text = ""
        }
        binding.bioField.setOnClickListener {
            binding.errorText.text = ""
        }



        // Yeha bata user ko data fetch gareko existing data user lai dekhauna








        // Update Profile Task Yeha Bata hai


        binding.registerHackFieButton.setOnClickListener {
            // Check gareko sabai field vareko xa ki nai vanerea

            val fullName = binding.fullNameField.text.toString().trim()
            val dobField = binding.dobField.text.toString().trim()
            val genderSelected = binding.genderSpinner.selectedItem.toString().trim()
            val statusSelected = binding.statusSpinner.selectedItem.toString().trim()
            val bioField = binding.bioField.text.toString().trim()
//            val passwordField = binding.confrimPasswordToUpdate.text.toString()

            // Variable chai user 10 barsa vanda sano ho ki hoina vanera check garna banako
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)

            if (fullName.isEmpty() || dobField.isEmpty() || genderSelected.isEmpty() || statusSelected.isEmpty() || bioField.isEmpty()) {

                binding.errorText.text = "Please fill all the fields"
            }else if(fullName.length < 3){
                binding.errorText.text = "Full Name must be at least 3 characters long"
            }

            else{

                val currentUser = FirebaseAuth.getInstance().currentUser
                val userId = currentUser?.uid.toString()


                if(profileUrl.isNullOrEmpty()){
                    profileUrl = previousProfileUrl
                }
                profileUrl?.let { it1 -> updateUserData(it1,userId, fullName, dobField, genderSelected, statusSelected, bioField) }
//                updateUserData(previousProfileUrl.toString(),userId, fullName, dobField, genderSelected, statusSelected, bioField)


            }





        }











        // Initialize the gender spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.genderSpinner.adapter = adapter
        }

        // Initialize the status spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.relationship_status_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.statusSpinner.adapter = adapter
        }

        // Handle gender spinner item selection
        binding.genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Handle the selected gender
                val gender = parent?.getItemAtPosition(position).toString()
                // You can perform actions based on the selected gender here
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // Handle status spinner item selection
        binding.statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Handle the selected status
                val status = parent?.getItemAtPosition(position).toString()
                // You can perform actions based on the selected status here
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // Set up date picker for date of birth
        binding.dobField.apply {
            isFocusable = false
            isFocusableInTouchMode = false
            inputType = 0 // Disable keyboard

            setOnClickListener {
                showDatePicker()
            }
        }

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack() // Go back to the previous fragment
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }


    private fun updateUserData(profileUrl: String ,userId: String, fullName: String, dobField: String, genderSelected: String, statusSelected: String, bioField: String) {

//        Toast.makeText(this.context, "Function is calling", Toast.LENGTH_SHORT).show()
        
        var image: String
        image = profileUrl
        if(profileUrl.isNullOrEmpty()){
            image = previousProfileUrl.toString()
        }

        val data: MutableMap<String, Any> = hashMapOf(
            "name" to fullName,
            "dob" to dobField,
            "gender" to genderSelected,
            "status" to statusSelected,
            "bio" to bioField,
            "profile" to image
        )

        val db = FirebaseFirestore.getInstance()
        db.collection(USERS_NODE)
            .document(userId)
            .update(data)
            .addOnSuccessListener {
                Toast.makeText(this.context, "Profile Update Successfully", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack() // Go back to the previous fragment
            }
            .addOnFailureListener { e ->
                Toast.makeText(this.context, "Sorry, Your Profile Update Failed", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack() // Go back to the previous fragment

            }
    }









    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        binding.dobField.setText("$dayOfMonth/${month + 1}/$year")
    }

    // User ko profile ko data fetch gareko
    private fun fetchData(userId: String) {
        val collectionReference = FirebaseFirestore.getInstance().collection("Users")

        collectionReference.document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val fullName = document.getString("name")
                    val dob = document.getString("dob")
                    val gender = document.getString("gender")
                    val status = document.getString("status")
                    val bio = document.getString("bio")
                    val profileUrl = document.getString("profile")
                    userPassword = document.getString("password")
                    previousProfileUrl = profileUrl


                    // Now you have the user data, you can use it as needed

                    // Yeha bata lyang lyang vayo data fetch gareko spinner handle gernu paro
                    // aba code handim spinner ko lagi

                    if (gender == "Male") {
                        binding.genderSpinner.setSelection(0)
                    } else if (gender == "Female") {
                        binding.genderSpinner.setSelection(1)
                    } else {
                        binding.genderSpinner.setSelection(2)
                    }

                    var selection = -1

                    when (status) {
                        "Single" -> selection = 0
                        "In a relationship" -> selection = 1
                        "Engaged" -> selection = 2
                        "Married" -> selection = 3
                        "Divorced" -> selection = 4
                        "Widowed" -> selection = 5
                        "Separated" -> selection = 6
                    }

                    if (selection != -1) {
                        binding.statusSpinner.setSelection(selection)
                    }



                    binding.fullNameField.setText(fullName)
                    binding.dobField.setText(dob)
//                    binding.genderSpinner.setSelection(gender)
//                    binding.statusSpinner.setSelection(status)
                    binding.bioField.setText(bio)

                    if(profileUrl.isNullOrEmpty()){
                        binding.updateUserProfileImage.setImageResource(R.drawable.user_avatar)
                    }else{
                        Glide.with(this /* context */)
                            .load(profileUrl)
                            .into(binding.updateUserProfileImage);
                    }






//                    Log.d("Firestore", "User data retrieved: $fullName, $emailAddress, $accountPassword, $registeredOn")
                } else {
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting document: ", exception)
            }
    }





}
