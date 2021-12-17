package com.mooveop.mooveopapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

class TestIssueActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var hoursList = remember { mutableStateListOf <String>("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23") }
            var hoursTextList = remember {mutableStateListOf <String> ()}
            var hoursExpandedList = remember {mutableStateListOf <Boolean> ()}
            var minutesTextList = remember {mutableStateListOf <String> ()}
            var minutesExpandedList = remember {mutableStateListOf <Boolean> ()}
            var numRows = remember {mutableStateOf (0)}
            Column() {
                repeat (numRows.value) { index ->
                    Row() {
                        OutlinedTextField(
                            value = minutesTextList[index],
                            onValueChange = {
                            },
                            readOnly = true,
                            modifier = Modifier
                                .clickable { minutesExpandedList[index] = true }
                                .width(140.dp)
                                .padding(start = 20.dp, end = 20.dp),
                            label = { Text("Label") },
                            trailingIcon = {
                                if (minutesExpandedList[index] == false) {
                                    IconButton(onClick = { minutesExpandedList[index] = true }) {
                                        Icon(
                                            imageVector = Icons.Outlined.ArrowDropDown,
                                            contentDescription = null
                                        )
                                    }
                                }
                                if (minutesExpandedList[index] == true) {
                                    IconButton(onClick = { minutesExpandedList[index] = false }) {
                                        Icon(
                                            imageVector = Icons.Outlined.ArrowBack,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = minutesExpandedList[index],
                            onDismissRequest = { minutesExpandedList[index] = false },
                            properties = PopupProperties(focusable = false),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            hoursList.forEach { label ->
                                DropdownMenuItem(onClick = {
                                    println("the index is ")
                                    println(index)
                                    minutesTextList[index] = label
//                                            updateCurrentRowHoursText ( riderRowsList[currentRowIndex].myIndex,label)
                                    minutesExpandedList[index] = false
                                }) {
                                    Text(text = label)
                                }
                            }
                        }
                        OutlinedTextField(
                            value = hoursTextList[index],
                            onValueChange = {
                            },
                            readOnly = true,
                            modifier = Modifier
                                .clickable { hoursExpandedList[index] = true }
                                .width(140.dp)
                                .padding(start = 20.dp, end = 20.dp),
                            label = { Text("Label") },
                            trailingIcon = {
                                if (hoursExpandedList[index] == false) {
                                    IconButton(onClick = { hoursExpandedList[index] = true }) {
                                        Icon(
                                            imageVector = Icons.Outlined.ArrowDropDown,
                                            contentDescription = null
                                        )
                                    }
                                }
                                if (hoursExpandedList[index] == true) {
                                    IconButton(onClick = { hoursExpandedList[index] = false }) {
                                        Icon(
                                            imageVector = Icons.Outlined.ArrowBack,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = hoursExpandedList[index],
                            onDismissRequest = { hoursExpandedList[index] = false },
                            properties = PopupProperties(focusable = false),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            hoursList.forEach { label ->
                                DropdownMenuItem(onClick = {
                                    println("the index is ")
                                    println(index)
                                    hoursTextList[index] = label
//                                            updateCurrentRowHoursText ( riderRowsList[currentRowIndex].myIndex,label)
                                    hoursExpandedList[index] = false
                                }) {
                                    Text(text = label)
                                }
                            }
                        }
                    }
                }
                Button(onClick = {
                    numRows.value  += 1
                    hoursTextList.add ("")
                    hoursExpandedList.add (false)
                    minutesTextList.add ("")
                    minutesExpandedList.add (false)
                }) {
                    Text ("Add Row")
                }
                }

                }
            }
        }



