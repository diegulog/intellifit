package com.diegulog.intellifit.data.repository.remote

import com.diegulog.intellifit.domain.entity.*
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.io.IOException
import java.lang.Thread.sleep

class ApiServiceMockInterceptor : Interceptor {
    private var codeResponse: Int = 200
    private val gson = Gson()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        this.codeResponse = 200
        val uri = chain.request().url.toUri().toString()
        val method = chain.request().method
        val requestBody = chain.request().body
        val responseString: String = when {
            uri.endsWith("/trainings") && method == "GET" -> createMockTrainings()
            uri.endsWith("/login/access-token") && method == "POST" -> processLogin(requestBody!!)
            uri.endsWith("signup") && method == "POST" -> processSignUp(requestBody!!)
            else -> "{\"status\":\"Unknown Response\"}"
        }
        //Simulamos  tiempo de espera
        sleep(2000)
        val response = Response.Builder()
            .code(codeResponse)
            .message(if (codeResponse == 200) "OK" else "Unauthorized")
            .request(chain.request())
            .protocol(Protocol.HTTP_2)
            .body(
                responseString.toByteArray()
                    .toResponseBody("application/json".toMediaTypeOrNull())
            )

        return response.build()
    }


    private fun processLogin(requestBody: RequestBody): String {
        val formBody = requestBody as FormBody
        val username = formBody.value(0)
        val password = formBody.value(1)
        return if (username == "user@gmail.com" && password == "12345") {
            gson.toJson(
                LoginResponse(
                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImFkbWluQGdtYWlsLmNvbSJ9.yNYJfXbdgdFiidf74nDVB3iXrJTsiJ4WiyUmvgCyRJI"
                )
            )
        } else if (username == "admin@gmail.com" && password == "12345"){
            gson.toJson(
                LoginResponse(
                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImFkbWluQGdtYWlsLmNvbSJ9.yNYJfXbdgdFiidf74nDVB3iXrJTsiJ4WiyUmvgCyRJI"
                )
            )
        } else {
            codeResponse = 401
            "{\"status\":\"Unauthorized\"}"
        }
    }


    private fun processSignUp(requestBody: RequestBody): String {
        val buffer = Buffer()
        requestBody.writeTo(buffer)
        val user = gson.fromJson(buffer.readUtf8(), User::class.java)
        return when (user.email) {
            "user@gmail.com", "admin@gmail.com" -> {
                codeResponse = 400
                """{ "detail": "User already exists" }"""
            }
            else -> {
                gson.toJson(user)
            }
        }
    }

    private fun createMockTrainings(): String {
        return Gson().toJson(trainingsMock)
    }

    companion object {
        val exercises0Mock = mutableListOf(
            Exercise(
                id = "3f791604-5a7f-47c9-8577-494380e51dc2",
                name = "Air Squat",
                urlImage = "https://persistenceathletics.com/wp-content/uploads/2020/05/bigstock-Girl-Doing-Squat-Exercise-Wom-362669806-scaled.jpg",
                urlVideo = "url_video_air_squat",
                duration = 2,
                idModel = "8453460833266057795",
                captures = emptyList(),
                isPublic = true,
                description = "Ponte de pie con los pies a la anchura de los hombros. Baja las caderas hacia atrás y hacia abajo, manteniendo la espalda recta y el pecho elevado. Luego regresa a la posición inicial.",
                trainingId = "01afecdb-b0de-46b3-ab2b-f796a493a9d2",
                repeat = 15
            ),
            Exercise(
                id = "c3f896b0-8dae-455d-8aab-2624099f9730",
                name = "Squat Jumps",
                urlImage = "https://img.freepik.com/premium-vector/jump-squat-exercise-woman-workout-fitness-aerobic-exercises_476141-711.jpg?w=2000",
                urlVideo = "url_video_squat_jumps",
                duration = 2,
                idModel = "3184025200255848001",
                captures = emptyList(),
                isPublic = true,
                description = "Ponte de pie con los pies a la anchura de los hombros. Haz una sentadilla y luego salta lo más alto que puedas. Aterriza con las rodillas flexionadas para amortiguar el impacto.",
                trainingId = "01afecdb-b0de-46b3-ab2b-f796a493a9d2",
                repeat = 15
            ),
            Exercise(
                id = "5b42f728-49cf-400d-be72-9751cf672bab",
                name = "Burpees",
                urlImage = "https://media.istockphoto.com/id/1286796612/es/vector/ejercicio-burpees-ejercicio-de-la-mujer-aer%C3%B3bico-y-ejercicios-ilustraci%C3%B3n-vectorial.jpg?s=612x612&w=0&k=20&c=lOcc6ptm_no5rDSMPSQObYui_CBooL5hhqECoP4ALjo=",
                urlVideo = "url_video_burpees",
                duration = 3,
                idModel = "3555357682168676141",
                captures = emptyList(),
                isPublic = true,
                description = "Desde una posición de pie, salta y ponte en posición de flexión de brazos, haz una flexión, vuelve a saltar para ponerte de pie",
                trainingId = "59730db0-b1da-4478-b91b-03db041e9472",
                repeat = 15
            ),
        )
        private val exercises1Mock = listOf(
            Exercise(
                id = "d4e74655-a7fd-48e2-8cab-ad268bdd7306",
                name = "High Knees",
                urlImage = "https://media.istockphoto.com/id/1314130951/vector/high-knees-exercise-woman-workout-fitness-aerobic-and-exercises-vector-illustration.jpg?s=170667a&w=0&k=20&c=EFs03hRln5OgAFDxyM1sOMGq2inzP7CwBCub29SDTI0=",
                urlVideo = "url_video_high_knees",
                duration = 1,
                idModel = "716015015213091302",
                captures = emptyList(),
                isPublic = true,
                description = "Correr en el lugar llevando las rodillas lo más alto posible",
                trainingId = "b11c346b-3136-4f17-a42d-7f78d56174b7",
                repeat = 15
            ),
            Exercise(
                id = "202ea328-2775-4096-b86f-44b2b3b2f18d",
                name = "Plank leg raises - left side",
                urlImage = "https://media1.popsugar-assets.com/files/thumbor/gkEOI0RBhSokrXnl3pCdcThd66Y/fit-in/728xorig/filters:format_auto-!!-:strip_icc-!!-/2013/12/17/713/n/1922729/e667013e1a76673e_side-plank-flip/i/Side-Plank-Leg-Lift-Left-Side.jpg",
                urlVideo = "url_video_plank_leg_raises_left",
                duration = 2,
                idModel = "6019807089751968608",
                captures = emptyList(),
                isPublic = true,
                description = "Colócate en posición de plancha lateral sobre tu lado derecho. Levanta la pierna izquierda hacia arriba y luego bájala sin que toque la otra pierna.",
                trainingId = "01afecdb-b0de-46b3-ab2b-f796a493a9d2",
                repeat = 15
            ),
            Exercise(
                id = "102ea328-2775-4196-b8ff-44b2bes2f18d",
                name = "Plank leg raises - right side",
                urlImage = "https://media1.popsugar-assets.com/files/thumbor/lcDIAcF82YoqR6MdBdAxqrCa13Y/fit-in/728xorig/filters:format_auto-!!-:strip_icc-!!-/2013/12/17/713/n/1922729/5aa7a350c9a78b68_side-plank-leg-lift/i/Side-Plank-Leg-Lift-Right-Side.jpg",
                urlVideo = "url_video_plank_leg_raises_right",
                duration = 2,
                idModel = "8357074708213445358",
                captures = emptyList(),
                isPublic = true,
                description = "Colócate en posición de plancha lateral sobre tu lado izquierdo. Levanta la pierna derecha hacia arriba y luego bájala sin que toque la otra pierna.",
                trainingId = "01afecdb-b0de-46b3-ab2b-f796a493a9d2",
                repeat = 15
            ),
            Exercise(
                id = "3097a0ae-225f-4e1c-a507-7d7839b9c40a",
                name = "Jumping Jacks",
                urlImage = "https://images.hola.com/imagenes/estar-bien/20200722172434/ejercicio-adelgazar-jumping-jacks-quemar-calorias/0-849-35/jumping-jack-2z-z.jpg",
                urlVideo = "url_video_jumping_jacks",
                duration = 1,
                idModel = "537067024020715589",
                captures = emptyList(),
                isPublic = true,
                description = "De pie, salta abriendo las piernas y elevando los brazos, luego salta de nuevo a la posición inicial.",
                trainingId = "9d2c7ee1-f24a-44e8-9236-a30d84becbdc",
                repeat = 15
            )
        )
        private val exercises2Mock =
            listOf(
                Exercise(
                    id = "04a07547-78a0-4a94-9f41-5b4c72e0634a",
                    name = "Push Ups",
                    urlImage = "https://media.istockphoto.com/id/1281672735/es/vector/mujer-haciendo-ejercicio-con-knee-push-up-en-2-pasos.jpg?s=170667a&w=0&k=20&c=7mMmc2ALL7qX12PE2sUs20gVHkR68lOOE391ibGq6KE=",
                    urlVideo = "url_video_push_ups",
                    duration = 2,
                    idModel = "323485527951887622",
                    captures = emptyList(),
                    isPublic = true,
                    description = "Posición de plancha, bajando y subiendo el cuerpo con los brazos",
                    trainingId = "6b72a508-280a-4b69-9678-41cefb59400c",
                    repeat = 15
                ),
                Exercise(
                    id = "58efa385-4de5-482f-8c6a-b32fe133b0d0",
                    name = "Squats",
                    urlImage = "https://media.istockphoto.com/id/985022798/vector/squat-sport-exersice-silhouettes-of-woman-doing-exercise-workout-training.jpg?s=1024x1024&w=is&k=20&c=eI0QoUWmyBJKShfL5orGbbXNwwbmNYaPnTKdsZDhkvQ=",
                    urlVideo = "url_video_squats",
                    duration = 2,
                    idModel = "5043439550877025664",
                    captures = emptyList(),
                    isPublic = true,
                    description = "De pie, bajar el cuerpo como si te fueras a sentar y luego subir",
                    trainingId = "52105029-e6e0-404c-9c89-eb6b39615187",
                    repeat = 15
                ),
                Exercise(
                    id = "a1f326eb-a54e-4f23-b089-c6a15d4751bc",
                    name = "Lunges",
                    urlImage = "https://media.istockphoto.com/id/1285221444/es/vector/caminar-ejercicio-lunge-entrenamiento-de-los-hombres-fitness-aer%C3%B3bico-y-ejercicios.jpg?s=1024x1024&w=is&k=20&c=GIe5qjM_EFary5fQJkI2GgsOlWC_N5DVfcbXfX_bhXA=",
                    urlVideo = "5927109199068676682",
                    duration = 2,
                    idModel = "modelo_lunges",
                    captures = emptyList(),
                    isPublic = true,
                    description = "De pie, dar un paso adelante y bajar el cuerpo, alternando las piernas",
                    trainingId = "0c5e1943-6def-4389-81e7-122dfe9b2a07",
                    repeat = 15
                ),
                Exercise(
                    id = "797a6240-7ada-4129-9c5a-33989aedbc3b",
                    name = "Plank Jacks",
                    urlImage = "https://media.istockphoto.com/id/1265941147/vector/plank-jacks-exercise-men-workout-fitness-aerobic-and-exercises-vector-illustration.jpg?s=170667a&w=0&k=20&c=i8tNTxyo6rVTUmj80NppuagkZIULCePLhsujBbdGTVs=",
                    urlVideo = "7961668557162104314",
                    duration = 1,
                    idModel = "modelo_plank_jacks",
                    captures = emptyList(),
                    isPublic = true,
                    description = "Desde posición de plancha, salta abriendo y cerrando las piernas",
                    trainingId = "5c430601-34c6-417f-ae36-08ea0c1546b3",
                    repeat = 15
                )

            )
        val trainingsMock = listOf(
            Training(
                id = "01afecdb-b0de-46b3-ab2b-f796a493a9d2",
                name = "Rutina de fuerza",
                urlImage = "https://media.istockphoto.com/id/1141568835/es/foto/mujeres-adultas-entrenando-piernas-haciendo-ejercicios-de-lunges-invertidos.jpg?s=612x612&w=0&k=20&c=Ys0XKUp77dVslS2cjQxCS_nRoHaHJtD8wGt5lfVZURQ=",
                exercises = exercises0Mock,
                isPublic = true,
                duration = 30,
                description = "Rutina de fuerza y resistencia centrada en la parte inferior del cuerpo y el core",
                ownerId = "01afecdb-b0de-46b3-ab2b-f796a493a9d2"
            ),
            Training(
                id = "ea0752dd-9448-490d-87e3-d5979b096059",
                name = "Rutina de Cardio Intenso",
                urlImage = "https://media.istockphoto.com/id/1305549525/es/foto/mujer-latina-haciendo-ejercicio-con-un-entrenamiento-cardiovascular.jpg?s=612x612&w=0&k=20&c=dBeBltCoyH9fzGBnrgPxWFzzKqXBI-vHMyPisZ63-ho=",
                exercises = exercises1Mock,
                isPublic = true,
                duration = 30,
                description = "Rutina intensiva de cardio para quemar grasa",
                ownerId = "adb4db06-b5cf-4f31-886b-89cc927655e9"
            ),
            Training(
                id = "c1763626-659b-4b32-9400-97810461a55e",
                name = "Rutina de Fuerza y Resistencia",
                urlImage = "https://images.unsplash.com/photo-1594381898411-846e7d193883?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTZ8fGVqZXJjaWNpb3xlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60",
                exercises = exercises2Mock,
                isPublic = true,
                duration = 45,
                description = "Rutina de fuerza y resistencia para tonificar músculos",
                ownerId = "66e290fb-2081-4355-91e2-b80e9a62b975"
            )
            // Aquí puedes agregar los dos entrenamientos restantes de manera similar
        )

    }

}