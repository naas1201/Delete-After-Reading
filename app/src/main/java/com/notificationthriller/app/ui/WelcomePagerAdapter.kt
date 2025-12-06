package com.notificationthriller.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.notificationthriller.app.R

/**
 * ViewPager2 adapter for welcome screens
 */
class WelcomePagerAdapter :
    RecyclerView.Adapter<WelcomePagerAdapter.WelcomeViewHolder>() {
    private val pages =
        listOf(
            WelcomePage(
                R.drawable.ic_notification_welcome,
                R.string.welcome_page1_title,
                R.string.welcome_page1_description,
            ),
            WelcomePage(
                R.drawable.ic_time_welcome,
                R.string.welcome_page2_title,
                R.string.welcome_page2_description,
            ),
            WelcomePage(
                R.drawable.ic_story_welcome,
                R.string.welcome_page3_title,
                R.string.welcome_page3_description,
            ),
            WelcomePage(
                R.drawable.ic_choices_welcome,
                R.string.welcome_page4_title,
                R.string.welcome_page4_description,
            ),
        )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): WelcomeViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_welcome_page, parent, false)
        return WelcomeViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: WelcomeViewHolder,
        position: Int,
    ) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size

    class WelcomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.welcomeIcon)
        private val title: TextView = itemView.findViewById(R.id.welcomeTitle)
        private val description: TextView = itemView.findViewById(R.id.welcomeDescription)

        fun bind(page: WelcomePage) {
            icon.setImageResource(page.iconRes)
            title.setText(page.titleRes)
            description.setText(page.descriptionRes)
        }
    }

    data class WelcomePage(
        val iconRes: Int,
        val titleRes: Int,
        val descriptionRes: Int,
    )
}
