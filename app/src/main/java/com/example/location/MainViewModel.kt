package com.example.location

import android.Manifest
import android.content.pm.PackageManager
import android.os.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.*
import com.google.android.gms.common.*
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*

class MainViewModel(
    private val activity: MainActivity
) : ViewModel(), GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private val _locationText = MutableLiveData<String>()
    val locationText
        get() = _locationText
    private val playServicesCode = 100
    private lateinit var googleApi: GoogleApiClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    init {
        if (checkPlayServices()) {
            connectToGoogle()
            initLocationRequest()
            initLocationCallback()
        }
    }

    private fun connectToGoogle() {
        googleApi = GoogleApiClient.Builder(activity)
            .addApi(LocationServices.API)
            .addOnConnectionFailedListener(this)
            .addConnectionCallbacks(this)
            .build()
        googleApi.connect()
    }

    private fun initLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
    }

    private fun initLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let { location ->
                    location.locations.forEach {
                        _locationText.value = "Latitude: ${it.latitude}, Longitude: ${it.longitude}"
                    }
                }
            }
        }
    }

    private fun checkPlayServices(): Boolean {
        val googleAvailability = GoogleApiAvailability.getInstance()
        val res = googleAvailability.isGooglePlayServicesAvailable(activity)

        if (res != ConnectionResult.SUCCESS) {
            if (googleAvailability.isUserResolvableError(res)) {
                googleAvailability.getErrorDialog(activity, res, playServicesCode)
            } else {
                Toast.makeText(
                    activity,
                    activity.resources.getString(R.string.not_supported),
                    Toast.LENGTH_SHORT
                ).show()
            }
            return false
        }
        return true
    }

    override fun onConnected(p0: Bundle?) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            ActivityCompat.requestPermissions(activity, permissions, 1000)
            return
        }

        LocationServices.getFusedLocationProviderClient(activity).requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        println("Connection Failed")
    }

    override fun onConnectionSuspended(p0: Int) {
        println("Connection Suspended")
    }

    override fun onCleared() {
        super.onCleared()
        if (googleApi.isConnected) googleApi.disconnect()
    }
}