package com.example.autogestion

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import com.example.autogestion.data.Repair
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autogestion.data.Vehicle
import com.example.autogestion.data.viewModels.RepairViewModel
import com.example.autogestion.data.viewModels.VehicleViewModel
import com.example.autogestion.form.ClientFormUpdate
import com.example.autogestion.form.VehicleFormUpdate
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class VehicleProfile : ComponentActivity() {

    private val vehicleViewModel: VehicleViewModel by viewModels()
    private val repairViewModel: RepairViewModel by viewModels()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val vehicleId = intent.getIntExtra("vehicleId", 0)

        setContent {
            //VehiclePage(vehicleId)
        }
    }

    private fun redirectToHome() {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        finish() // Ferme l'activité actuelle pour éviter le retour avec le bouton "Back"
    }

    @Composable
    fun VehiclePage(/*vehicleId: Int*/ vehicle : Vehicle?, repairList : List<Repair>) {
        val context = LocalContext.current
        //var vehicle = vehicleViewModel.getVehicleById(vehicleId).value
        //var repairList = repairViewModel.getRepairsFromVehicle(vehicleId)

        val coroutineScope = rememberCoroutineScope()

        Column(modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()) {

            NavBar(text = "Profile Voiture") {
                val intent = Intent(context, ClientProfile::class.java).apply {
                    putExtra("clientId", vehicle?.clientId)
                }
                context.startActivity(intent)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Car information
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${vehicle?.brand}, ${vehicle?.model}",
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Couleur : ${vehicle?.color}",
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Num. de chassis : ${vehicle?.chassisNum}",
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Plaque: ${vehicle?.registrationPlate}",
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // TODO add link to grey card
                    Text(
                        text = "Carte grise: ${vehicle?.greyCard}",
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Row {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            vehicleViewModel.deleteVehicle(vehicle!!)
                            redirectToHome()
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_delete_24),
                            contentDescription = "Supprimer",
                            tint = Color.Black
                        )
                    }

                    IconButton(onClick = {
                        val intent = Intent(context, VehicleFormUpdate::class.java).apply {
                            putExtra("vehicleId", vehicle!!.vehicleId)
                        }
                        context.startActivity(intent)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_edit_24),
                            contentDescription = "Modifier",
                            tint = Color.Black
                        )
                    }
                }
            }

            // Header add button + text
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Liste Pannes",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                // TODO
                IconButton(onClick = { println("TODO") })
                {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_24),
                        contentDescription = "Ajouter",
                        tint = Color.Black
                    )
                }
            }


            // Divide profile and car list
            Divider(
                color = Color.Gray, // Couleur de la ligne
                thickness = 2.dp,   // Épaisseur de la ligne
            )

            // Repair List
            LazyColumn(modifier = Modifier
                .padding(16.dp)
                .wrapContentSize()) {

                items(repairList.size) { index ->
                    repairList[index]?.let { RepairItem(it) }
                    if (index < repairList.size - 1) {

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    // One Car
    @Composable
    fun RepairItem(repair: Repair) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(color = Color(0xFFF3EDF7))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){

            Column(modifier = Modifier.width(250.dp)) {
                Text(text = "${repair.description}", modifier = Modifier.padding(bottom = 4.dp))
                Text(text = "Date reparation : ", modifier = Modifier.padding(bottom = 4.dp))
                Text(text = "Facture" , modifier = Modifier.padding(bottom = 4.dp))
            }

            Row {
                IconButton(onClick = {/*TODO*/}) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_delete_24),
                        contentDescription = "Supprimer",
                        tint = Color.Black
                    )
                }
                IconButton(onClick = {/*TODO*/}) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_edit_24),
                        contentDescription = "Modifier",
                        tint = Color.Black
                    )
                }
            }

        }

    }

    @Preview(showBackground = true)
    @Composable
    fun ProfilePrew() {

        val vehicle = Vehicle(
            vehicleId = 1,               // L'ID du véhicule (auto-généré)
            clientId = 123,              // ID du client associé
            chassisNum = "1234567890987654",    // Numéro de châssis
            greyCard = "/url",     // Numéro de carte grise
            registrationPlate = "AB-123-CD", // Plaque d'immatriculation
            brand = "Toyota",            // Marque du véhicule
            model = "Corolla",           // Modèle du véhicule
            color = "Bleu"               // Couleur du véhicule
        )


        val listeRepar = listOf(
            Repair(
                repairId = 1,                // ID de la réparation (auto-généré)
                vehicleId = 101,             // ID du véhicule associé
                description = "Changement des plaquettes de frein", // Description de la réparation
                date = 1672531200000,        // Date de la réparation en timestamp (ex. 01/01/2023)
                invoice = "INV-2023-001",    // Numéro de la facture
                paid = true                  // La réparation est-elle payée ?
            ),

            Repair(
                repairId = 2,                // ID de la réparation (auto-généré)
                vehicleId = 102,             // ID du véhicule associé
                description = "Remplacement de la batterie",  // Description de la réparation
                date = 1675209600000,        // Date de la réparation en timestamp (ex. 02/02/2023)
                invoice = "INV-2023-002",    // Numéro de la facture
                paid = false                 // La réparation est-elle payée ?
            ),
            Repair(
                repairId = 3,                // ID de la réparation (auto-généré)
                vehicleId = 103,             // ID du véhicule associé
                description = "Révision complète",   // Description de la réparation
                date = 1677628800000,        // Date de la réparation en timestamp (ex. 01/03/2023)
                invoice = "INV-2023-003",    // Numéro de la facture
                paid = true                  // La réparation est-elle payée ?
            )
        )

        VehicleProfile().VehiclePage(vehicle = vehicle, repairList = listeRepar)

    }
}