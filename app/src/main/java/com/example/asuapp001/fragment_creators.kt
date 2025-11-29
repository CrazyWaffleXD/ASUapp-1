package com.example.asuapp001

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.asuapp001.R
import com.example.asuapp001.databinding.FragmentFragmentCreatorsBinding
import com.example.asuapp001.data.Developer // Используем общий класс
import com.example.asuapp001.ui.adapters.DeveloperAdapter

class fragment_creators : Fragment() {

    private var _binding: FragmentFragmentCreatorsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFragmentCreatorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Пример данных
        val developers = listOf(
            Developer(
                name = "Земцов Алексей Владимирович",
                role = "Руководитель проекта",
                group = "Преподаватель СПО",
                descriptionText = "Наш слон",
                avatarResId = R.drawable.ic_avatar_default,
                githubUrl = "https://github.com/alexey",
                email = "alexey@asu.ru"
            ),
            Developer(
                name = "Затеев Николай Максимович",
                role = "Разработчик, дизайнер",
                group = "305с9-1",
                descriptionText = "Описания нет",
                avatarResId = R.drawable.ic_avatar_default,
                githubUrl = "",
                email = "nikolay.zateev@yandex.ru"
            ),
            Developer(
                name = "Панин Александр Николаевич",
                role = "Лидер проекта, разработчик",
                group = "305с9-1",
                descriptionText = "",
                avatarResId = R.drawable.ic_avatar_default,
                githubUrl = "",
                email = "sasha.pannin@yandex.ru"
            ),
            Developer(
                name = "Швецов Аркадий Александрович",
                role = "Разработчик, тестировщик",
                group = "305с9-2",
                descriptionText = "Описания нет",
                avatarResId = R.drawable.ic_avatar_default,
                githubUrl = "",
                email = "arkadiy.shevtsow@yandex.ru"
            ),
            Developer(
                name = "Хуторной Михаил Николаевич",
                role = "Разработчик",
                group = "305с9-1",
                descriptionText = "Описания нет",
                avatarResId = R.drawable.ic_avatar_default,
                githubUrl = "",
                email = "mikhail.khutornoy@yandex.ru"
            ),
            Developer(
                name = "Черданцев Иван",
                role = "Разработчик",
                group = "305с9-1",
                descriptionText = "Описания нет",
                avatarResId = R.drawable.ic_avatar_default,
                githubUrl = "",
                email = "ivan.cherdantsev@yandex.ru"
            ),
            Developer(
                name = "Шабанов Никита",
                role = "Разработчик, дизайнер",
                group = "405с11-с",
                descriptionText = "Описания нет",
                avatarResId = R.drawable.ic_avatar_default,
                githubUrl = "",
                email = "nikita.shabanov@yandex.ru"
            )
        )

        // Настройка RecyclerView
        binding.recyclerViewDevelopers.apply {
            adapter = DeveloperAdapter(developers)
            isNestedScrollingEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}