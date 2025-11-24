package com.example.asuapp001 // ⚠️ Замените на ваш пакет

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.asuapp001.R // ⚠️ Убедитесь, что R импортирован
import com.example.asuapp001.data.Developer // ⚠️ Убедитесь, что Developer импортирован
import com.example.asuapp001.ui.adapters.DeveloperAdapter // ⚠️ Убедитесь, что DeveloperAdapter импортирован

class fragment_creators : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DeveloperAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fragment_creators, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewDevelopers)

        // Создаем список разработчиков
        val developers = listOf(
            Developer(
                name = "Земцов Алексей Владимирович",
                role = "Руководитель проекта",
                group = "Преподаватель СПО",
                githubUrl = "https://github.com/zemcov",
                email = "zemcov@example.com",
                avatarResId = R.drawable.ic_teacher // ⚠️ Убедитесь, что иконка существует
            ),
            Developer(
                name = "Панин Александр Николаевич",
                role = "Главный разработчик",
                group = "305с9-1",
                githubUrl = "https://github.com/panin",
                email = "panin@example.com",
                avatarResId = R.drawable.ic_dev_laptop
            ),
            Developer(
                name = "Шабанов Никита Андреевич",
                role = "UI/UX дизайнер",
                group = "405с11-1",
                githubUrl = "https://github.com/shabanov",
                email = "shabanov@example.com",
                avatarResId = R.drawable.ic_designer
            ),
            Developer(
                name = "Черданцев Иван Евгеньевич",
                role = "Разработчик",
                group = "305с9-1",
                githubUrl = "https://github.com/cherdantsev",
                email = "cherdantsev@example.com",
                avatarResId = R.drawable.ic_dev_gear
            ),
            Developer(
                name = "Хуторной Михаил Николаевич",
                role = "Разработчик",
                group = "305с9-1",
                githubUrl = "https://github.com/hutornoi",
                email = "hutornoi@example.com",
                avatarResId = R.drawable.ic_dev_gear


            ),
            Developer(
                name = "Швецов аркадий Александрович",
                role = "Разработчик",
                group = "305с9-1",
                githubUrl = "https://github.com/shevehov",
                email = "shevehov@example.com",
                avatarResId = R.drawable.ic_dev_gear
            ),
            Developer(
                name = "Затеев Николай Максимович ",
                role = "Разработчик",
                group = "305с9-1",
                githubUrl = "https://github.com/zateev",
                email = "zateev@example.com",
                avatarResId = R.drawable.ic_dev_gear

            )
        )

        adapter = DeveloperAdapter(developers)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context) // Установка layoutManager
    }
}