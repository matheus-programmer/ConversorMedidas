package com.example.conversormedidas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.conversormedidas.ui.theme.ConversorMedidasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConversorMedidasTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ConversorDeUnidades()
                }
            }
        }
    }
}

@Composable
fun ConversorDeUnidades() {
    val unidadesMap = mapOf(
        "Centímetros (cm)" to "cm",
        "Metros (m)" to "m",
        "Quilômetros (km)" to "km",
        "Milhas (milha)" to "milha"
    )
    val nomesUnidades = unidadesMap.keys.toList()
    var unidadeEntrada by remember { mutableStateOf(nomesUnidades[1]) } // "Metros (m)"
    var unidadeSaida by remember { mutableStateOf(nomesUnidades[2]) }   // "Quilômetros (km)"
    var valorEntrada by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Text(
                    text = "Conversor de Unidades",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                UnidadeDropdown("Converter de:", unidadeEntrada, nomesUnidades) { unidadeEntrada = it }
                UnidadeDropdown("Para:", unidadeSaida, nomesUnidades) { unidadeSaida = it }

                OutlinedTextField(
                    value = valorEntrada,
                    onValueChange = { valorEntrada = it },
                    label = { Text("Valor a converter") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Button(
                    onClick = {
                        val resultadoValor = converter(
                            valorEntrada,
                            unidadesMap[unidadeEntrada] ?: "",
                            unidadesMap[unidadeSaida] ?: ""
                        )
                        resultado = "${formatarResultado(resultadoValor)} ${unidadesMap[unidadeSaida]}"

                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Converter")
                }

                if (resultado.isNotEmpty()) {
                    Surface(
                        tonalElevation = 4.dp,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Resultado: $resultado",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnidadeDropdown(label: String, selected: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selected,
                onValueChange = {},
                label = { Text("Unidade") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { unidade ->
                    DropdownMenuItem(
                        text = { Text(unidade) },
                        onClick = {
                            onSelect(unidade)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

fun converter(valorStr: String, de: String, para: String): String {
    val valor = valorStr.toDoubleOrNull() ?: return "Valor inválido"

    // Tudo em metros como unidade base
    val emMetros = when (de) {
        "cm" -> valor / 100
        "m" -> valor
        "km" -> valor * 1000
        "milha" -> valor * 1609.34
        else -> return "Unidade inválida"
    }

    val convertido = when (para) {
        "cm" -> emMetros * 100
        "m" -> emMetros
        "km" -> emMetros / 1000
        "milha" -> emMetros / 1609.34
        else -> return "Unidade inválida"
    }

    return "%.4f".format(convertido)
}

fun formatarResultado(valor: String): String {
    val numero = valor.toDoubleOrNull() ?: return valor
    return if (numero % 1.0 == 0.0) {
        "%.0f".format(numero)
    } else {
        "%.2f".format(numero)
    }
}