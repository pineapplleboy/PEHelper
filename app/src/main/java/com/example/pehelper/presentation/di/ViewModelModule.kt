package com.example.pehelper.presentation.di

import com.example.pehelper.presentation.screen.AllAttendancesViewModel
import com.example.pehelper.presentation.screen.AuthViewModel
import com.example.pehelper.presentation.screen.AvatarViewModel
import com.example.pehelper.presentation.screen.CuratorApplicationsViewModel
import com.example.pehelper.presentation.screen.ProfileViewModel
import com.example.pehelper.presentation.screen.SportsEventsViewModel
import com.example.pehelper.presentation.screen.StudentEventDetailViewModel
import com.example.pehelper.presentation.screen.StudentPairsViewModel
import com.example.pehelper.presentation.screen.StudentProfileViewModel
import com.example.pehelper.presentation.screen.TeacherPairsViewModel
import com.example.pehelper.presentation.viewmodel.LessonStudentsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AuthViewModel() }
    viewModel { ProfileViewModel() }
    viewModel { SportsEventsViewModel() }
    viewModel { TeacherPairsViewModel() }
    viewModel { (lessonId: String) -> LessonStudentsViewModel(lessonId) }
    viewModel { StudentPairsViewModel() }
    viewModel { StudentEventDetailViewModel() }
    viewModel { AvatarViewModel() }
    viewModel { AllAttendancesViewModel() }
    viewModel { StudentProfileViewModel() }
    viewModel { CuratorApplicationsViewModel() }
    viewModel { com.example.pehelper.presentation.screen.CuratorStudentProfileViewModel() }
    viewModel { com.example.pehelper.presentation.screen.CuratorGroupsViewModel() }
}