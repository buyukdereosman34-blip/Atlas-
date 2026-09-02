package com.atlas.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atlas.app.databinding.ActivityAppsBinding

class AppsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppsBinding
    private lateinit var controller: AppsController
    private lateinit var adapter: AppsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAppsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        controller = AppsController(this)

        setupToolbar()
        setupRecyclerView()
        setupActions()
        loadApps()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarApps)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        adapter = AppsAdapter { app ->
            openAppDetail(app)
        }

        binding.recyclerApps.layoutManager = LinearLayoutManager(this)
        binding.recyclerApps.adapter = adapter
    }

    private fun setupActions() {
        binding.btnRefreshApps.setOnClickListener {
            loadApps()
        }
    }

    private fun loadApps() {
        try {
            val apps = controller.getInstalledApps()

            adapter.submitList(apps)

            binding.tvAppCount.text =
                "Yüklü uygulamalar: ${apps.size}"

        } catch (exception: Exception) {

            binding.tvAppCount.text =
                "Uygulamalar okunamadı"

            Toast.makeText(
                this,
                "Uygulama listesi alınamadı",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openAppDetail(app: AppsController.AppInfo) {

        val intent = Intent(
            this,
            AppDetailActivity::class.java
        ).apply {

            putExtra(
                AppDetailActivity.EXTRA_PACKAGE_NAME,
                app.packageName
            )

            putExtra(
                AppDetailActivity.EXTRA_LABEL,
                app.label
            )

            putExtra(
                AppDetailActivity.EXTRA_IS_SYSTEM,
                app.isSystemApp
            )
        }

        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

private class AppsAdapter(
    private val onAppClick: (AppsController.AppInfo) -> Unit
) : RecyclerView.Adapter<AppsAdapter.AppViewHolder>() {

    private var items: List<AppsController.AppInfo> = emptyList()

    fun submitList(
        newItems: List<AppsController.AppInfo>
    ) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_app,
                parent,
                false
            )

        return AppViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AppViewHolder,
        position: Int
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class AppViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        private val icon: ImageView =
            view.findViewById(R.id.iv_app_icon)

        private val title: TextView =
            view.findViewById(R.id.tv_app_name)

        private val packageName: TextView =
            view.findViewById(R.id.tv_app_package)

        private val type: TextView =
            view.findViewById(R.id.tv_app_type)

        fun bind(app: AppsController.AppInfo) {

            icon.setImageDrawable(app.icon)

            title.text = app.label

            packageName.text = app.packageName

            type.text =
                if (app.isSystemApp) {
                    "Sistem uygulaması"
                } else {
                    "Kullanıcı uygulaması"
                }

            itemView.setOnClickListener {
                onAppClick(app)
            }
        }
    }
}
