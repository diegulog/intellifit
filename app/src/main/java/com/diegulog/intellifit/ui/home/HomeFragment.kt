package com.diegulog.intellifit.ui.home

import android.opengl.Visibility
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.diegulog.intellifit.R
import com.diegulog.intellifit.databinding.FragmentHomeBinding
import com.diegulog.intellifit.databinding.NavHeaderHomeFragmentBinding
import com.diegulog.intellifit.ui.MainActivity
import com.diegulog.intellifit.ui.base.BaseFragment
import com.diegulog.intellifit.ui.login.LoginViewModel
import com.google.android.material.navigation.NavigationView
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val loginViewModel: LoginViewModel by activityViewModel()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val nestedNavHostFragment = childFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_home_fragment) as NavHostFragment?
        navController = nestedNavHostFragment!!.navController
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.TrainingFragment), drawerLayout
        )
        setupWithNavController(binding.toolbar, navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener {menu ->
            when(menu.itemId){
                R.id.nav_close_session -> onCloseSession()
            }
            false

        }

        loginViewModel.getUser()?.let {
            val header = NavHeaderHomeFragmentBinding.bind(binding.navView.getHeaderView(0))
            header.username.text= it
        }
    }

    private fun onCloseSession() {
        loginViewModel.closeSession()
        Navigation.findNavController(requireActivity(),
            R.id.nav_host_fragment_content_main).navigate(R.id.action_HomeFragment_to_LoginFragment)
    }

    fun setTitle(title: String){
        binding.toolbar.setTitle(title)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        // handle the up button here
        return NavigationUI.onNavDestinationSelected(item,
            navController)
                || super.onOptionsItemSelected(item)
    }

}