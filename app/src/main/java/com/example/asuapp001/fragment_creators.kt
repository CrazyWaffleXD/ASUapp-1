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
                name = "Алексей",
                role = "Android-разработчик",
                group = "305с9-1",
                descriptionText = "Описания нет",
                avatarResId = R.drawable.ic_avatar_default,
                githubUrl = "https://github.com/alexey",
                email = "alexey@asu.ru"
            ),
            Developer(
                name = "Марина",
                role = "UI/UX Дизайнер",
                group = "405с11-1",
                descriptionText = "Описания нет",
                avatarResId = R.drawable.ic_avatar_default,
                githubUrl = "https://github.com/marina",
                email = "marina@asu.ru"
            ),
            Developer(
                name = "имя",
                role = "Роль",
                group = "группа",
                descriptionText = "Описания нет",
                avatarResId = R.drawable.ic_avatar_default,
                githubUrl = "https://github.com/marina",
                email = "marina@asu.ru"
            ),
            Developer(
                name = "имя",
                role = "Роль",
                group = "группа",
                descriptionText = "Описания нет",
                avatarResId = R.drawable.ic_avatar_default,
                githubUrl = "https://github.com/marina",
                email = "marina@asu.ru"
            ),
            Developer(
                name = "имя",
                role = "Роль",
                group = "группа",
                descriptionText = "Описания нет",
                avatarResId = R.drawable.ic_avatar_default,
                githubUrl = "https://github.com/marina",
                email = "marina@asu.ru"
            ),
            Developer(
                name = "имя",
                role = "Роль",
                group = "группа",
                descriptionText = "Описания нет",
                avatarResId = R.drawable.ic_avatar_default,
                githubUrl = "https://github.com/marina",
                email = "marina@asu.ru"
            ),
            Developer(
                name = "имя",
                role = "Роль",
                group = "группа",
                descriptionText = "Описания нет",
                avatarResId = R.drawable.ic_avatar_default,
                githubUrl = "https://github.com/marina",
                email = "marina@asu.ru"
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