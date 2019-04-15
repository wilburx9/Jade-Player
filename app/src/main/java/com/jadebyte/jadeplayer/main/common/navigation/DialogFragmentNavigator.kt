// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.navigation

/**
 * Created by Wilberforce on 09/04/2019 at 23:47.
 */
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.core.content.res.use
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.jadebyte.jadeplayer.R
import java.util.*

/**
 * Allows to navigate to some [DialogFragment].
 *
 * Usage: add some dialog element in your navigation graph
 * ```
 *     <dialog-fragment
 *          android:id="@+id/my_dialog"
 *          android:name="com.exemple.MyDialogFragment"
 *          tools:layout="@layout/fragment_my_dialog" />
 *
 * ```
 * Use [CustomNavHostFragment] as your [androidx.navigation.NavHost] in your layout
 *
 * Source: https://gist.github.com/matpag/74ce059d590ff571c8240428b274c8c5
 */
@Navigator.Name("dialog-fragment")
class DialogFragmentNavigator(
    private val fragmentManager: FragmentManager
) : Navigator<DialogFragmentNavigator.Destination>() {

    private var lastBackStackEntry: FragmentManager.BackStackEntry? = null
    private val backStack: Deque<Int> = ArrayDeque()
    private var pendingPopBackStack = false

    private val onBackStackChangedListener: FragmentManager.OnBackStackChangedListener =
        FragmentManager.OnBackStackChangedListener {
            if (pendingPopBackStack) {
                val entry = fragmentManager.findLastBackStackEntry { it.name == FRAGMENT_BACK_STACK_NAME }
                pendingPopBackStack = (entry != null && entry == lastBackStackEntry)
                lastBackStackEntry = entry
                return@OnBackStackChangedListener
            }
            if (lastBackStackEntry != null && fragmentManager.noneBackStackEntry { it == lastBackStackEntry }) {
                backStack.removeLast()
            }
            lastBackStackEntry = fragmentManager.findLastBackStackEntry { it.name == FRAGMENT_BACK_STACK_NAME }
        }

    override fun navigate(
        destination: Destination, args: Bundle?, navOptions: NavOptions?, navigatorExtras: Extras?
    ): NavDestination? {
        val lastDialogFragment = instantiateFragment(destination.className!!).apply { arguments = args }
        val tr = fragmentManager.beginTransaction().addToBackStack(FRAGMENT_BACK_STACK_NAME)

        lastDialogFragment.show(tr, destination.id.toString())
        backStack.addLast(destination.id)

        // This means that the destination will be added to the NavController stack, it will update the whole
        // navigation chrome (global AppBar, NavigationView, etc)
        return createDestination()
    }

    private fun instantiateFragment(className: String): DialogFragment {
        val clazz = Class.forName(className)
        return fragmentManager.fragmentFactory
            .instantiate(clazz.classLoader!!, className) as DialogFragment
    }

    override fun createDestination(): Destination = Destination(this)

    override fun popBackStack(): Boolean {
        val lastDialogFragment =
            fragmentManager.findFragmentByTag(backStack.lastOrNull()?.toString()) as? DialogFragment ?: return false
        lastDialogFragment.dismiss()
        backStack.removeLast()
        pendingPopBackStack = true
        return true
    }

    /* These 2 lifecycle methods should be handled in the NavHost of this navigator.
     * When the NavHost add them it should configure them or call some method so
     * that they can configure there listeners. However, this make a strong coupling between a Navigator and
     * its host implementation.
     *
     * The NavigatorProvider of NavController add a Navigator.OnBackStackChangedListener
     * who calls these methods */
    override fun onBackPressAdded() {
        fragmentManager.addOnBackStackChangedListener(onBackStackChangedListener)
    }

    override fun onBackPressRemoved() {
        fragmentManager.removeOnBackStackChangedListener(onBackStackChangedListener)
    }

    override fun onSaveState(): Bundle? {
        return bundleOf(KEY_BACK_STACK_ID to backStack.toIntArray())
    }

    override fun onRestoreState(savedState: Bundle) {
        savedState.getIntArray(KEY_BACK_STACK_ID)?.let {
            backStack.clear()
            for (id in it) {
                backStack.addLast(id)
            }
        }
        lastBackStackEntry = fragmentManager.findLastBackStackEntry { it.name == FRAGMENT_BACK_STACK_NAME }
    }

    class Destination(navigator: DialogFragmentNavigator) : NavDestination(navigator) {
        var className: String? = null
            get() = checkNotNull(field) { "Dialog name was not set" }

        override fun onInflate(context: Context, attrs: AttributeSet) {
            super.onInflate(context, attrs)
            context.resources.obtainAttributes(attrs, R.styleable.DialogFragmentNavigator).use {
                className = it.getString(R.styleable.DialogFragmentNavigator_android_name)
            }
        }
    }

    companion object {
        private const val KEY_BACK_STACK_ID = "com.jadebyte.jadeplayer:navigation:back_stack_ids"
        private const val FRAGMENT_BACK_STACK_NAME = "com.jadebyte.jadeplayer:navigation:back_stack"
    }

    private inline fun FragmentManager.findLastBackStackEntry(
        predicate: (FragmentManager.BackStackEntry) -> Boolean
    ): FragmentManager.BackStackEntry? {
        for (i in backStackEntryCount - 1 downTo 0) {
            val backStackEntry = getBackStackEntryAt(i)
            if (predicate(backStackEntry)) {
                return backStackEntry
            }
        }
        return null
    }

    private inline fun FragmentManager.noneBackStackEntry(
        predicate: (FragmentManager.BackStackEntry) -> Boolean
    ): Boolean {
        for (i in 0 until backStackEntryCount) {
            val backStackEntry = getBackStackEntryAt(i)
            if (predicate(backStackEntry)) {
                return false
            }
        }
        return true
    }
}