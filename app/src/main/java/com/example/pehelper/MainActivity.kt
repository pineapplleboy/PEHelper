package com.example.pehelper

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pehelper.presentation.screen.AuthScreen
import com.example.pehelper.presentation.screen.CreateLessonScreen
import com.example.pehelper.presentation.screen.CuratorProfileScreen
import com.example.pehelper.presentation.screen.ProfileScreen
import com.example.pehelper.presentation.screen.SplashScreen
import com.example.pehelper.presentation.screen.SportsOrganizerProfileScreen
import com.example.pehelper.presentation.screen.SportsEventsScreen
import com.example.pehelper.presentation.screen.CreateSportsEventScreen
import com.example.pehelper.presentation.screen.LessonStudentsScreen
import com.example.pehelper.presentation.screen.SportLessonsScreen
import com.example.pehelper.presentation.screen.SportsEventDetailScreen
import com.example.pehelper.presentation.screen.StudentPairsScreen
import com.example.pehelper.presentation.screen.AllAttendancesScreen
import com.example.pehelper.presentation.screen.StudentEventDetailScreen
import com.example.pehelper.presentation.screen.StudentProfileScreen
import com.example.pehelper.presentation.screen.CuratorApplicationsScreen
import com.example.pehelper.ui.theme.PEHelperTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PEHelperTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        composable("splash") { SplashScreen(navController = navController) }
                        composable("auth") { AuthScreen(navController = navController) }
                        composable("profile") { ProfileScreen(navController = navController) }
                        composable("student_profile") { StudentProfileScreen(navController = navController) }
                        composable("curator_profile") { 
                            CuratorProfileScreen(
                                navController = navController,
                                onBack = { navController.popBackStack() }
                            ) 
                        }
                        composable("sports_organizer_profile") {
                            SportsOrganizerProfileScreen(
                                navController = navController
                            )
                        }
                        composable("student_pairs") {
                            StudentPairsScreen(
                                onProfileClick = {
                                    navController.navigate("student_profile")
                                },
                                onEventClick = { eventId ->
                                    navController.navigate("student_event_detail/$eventId")
                                },
                                navController = navController
                            )
                        }
                        composable("sport_lessons") {
                            SportLessonsScreen(
                                onProfileClick = {
                                    navController.navigate("profile")
                                },
                                navController = navController
                            )
                        }
                        composable(
                            "lesson_students/{lessonId}/{lessonTime}/{lessonTitle}",
                            arguments = listOf(
                                navArgument("lessonId") { type = NavType.StringType },
                                navArgument("lessonTime") { type = NavType.StringType },
                                navArgument("lessonTitle") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            LessonStudentsScreen(
                                lessonId = backStackEntry.arguments?.getString("lessonId") ?: "",
                                lessonTime = backStackEntry.arguments?.getString("lessonTime") ?: "",
                                lessonTitle = backStackEntry.arguments?.getString("lessonTitle") ?: "",
                                navController = navController
                            )
                        }
                        composable("create_lesson") {
                            CreateLessonScreen(
                                onCreated = { navController.popBackStack() },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("sports_events") {
                            SportsEventsScreen(
                                onCreateEventClick = { navController.navigate("create_sports_event") },
                                onProfileClick = {
                                    navController.navigate("sports_organizer_profile")
                                },
                                navController = navController
                            )
                        }
                        composable("create_sports_event") {
                            CreateSportsEventScreen(
                                onCreateClick = { navController.popBackStack() },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("sports_event_detail/{eventId}") { backStackEntry ->
                            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                            SportsEventDetailScreen(
                                eventId = eventId,
                                onBack = { navController.popBackStack() },
                                onDeleted = {
                                    navController.popBackStack("sports_events", false)
                                }
                            )
                        }
                        composable("all_attendances") {
                            AllAttendancesScreen(navController = navController)
                        }
                        composable("student_event_detail/{eventId}") { backStackEntry ->
                            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                            StudentEventDetailScreen(
                                eventId = eventId,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable("curator_applications") {
                            CuratorApplicationsScreen(
                                onProfileClick = {
                                    navController.navigate("curator_profile")
                                },
                                onGroupsClick = {
                                    navController.navigate("curator_groups_input")
                                },
                                navController = navController
                            )
                        }
                        composable("curator_groups_input") {
                            com.example.pehelper.presentation.screen.CuratorGroupInputScreen(
                                onBack = { navController.popBackStack() },
                                onGroupSelected = { groupNumber ->
                                    navController.navigate("curator_group_detail/$groupNumber")
                                }
                            )
                        }
                        composable(
                            "curator_group_detail/{groupNumber}",
                            arguments = listOf(navArgument("groupNumber") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val groupNumber = backStackEntry.arguments?.getString("groupNumber") ?: ""
                            com.example.pehelper.presentation.screen.CuratorGroupDetailScreen(
                                groupNumber = groupNumber,
                                onBack = { navController.popBackStack() },
                                onStudentClick = { studentId ->
                                    navController.navigate("curator_student_profile/$studentId")
                                }
                            )
                        }
                        composable(
                            "curator_student_profile/{studentId}",
                            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
                            com.example.pehelper.presentation.screen.CuratorStudentProfileScreen(
                                studentId = studentId,
                                onBack = { navController.popBackStack() },
                                onViewAllAttendances = { studentIdForAttendances ->
                                    navController.navigate("curator_student_attendances/$studentIdForAttendances")
                                }
                            )
                        }
                        composable(
                            "curator_student_attendances/{studentId}",
                            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
                            com.example.pehelper.presentation.screen.CuratorStudentAttendancesScreen(
                                studentId = studentId,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PEHelperTheme {
        SplashScreen(rememberNavController())
    }
}