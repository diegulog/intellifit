# IntelliFit

IntelliFit, una aplicación móvil para guiar a los usuarios en la ejecución correcta de ejercicios físicos utilizando inteligencia artificial y técnicas de detección de movimiento para analizar y proporcionar retroalimentación en tiempo real sobre la postura y ejecución de los ejercicios.

La arquitectura de la aplicación esta basada en el patrón MVVM (Model-View-ViewModel) de Android, el cual permite separar la lógica de la vista y facilita la creación de aplicaciones escalables y mantenibles.

En este patrón, la vista representa la capa de presentación, la cual se encarga de interactuar con el usuario a través de la interfaz gráfica de la aplicación. El modelo representa los datos y la lógica de negocio, mientras que el ViewModel es el encargado de conectar la vista con el modelo y manejar la lógica específica de la presentación.

La arquitectura se dividirá en capas, lo que permitirá una fácil gestión y mantenimiento de la aplicación. Las capas serán las siguientes:

**•	Capa de presentación:** Esta capa estará compuesta por las vistas y los ViewModels. Los ViewModels contendrán la lógica de presentación y se comunicarán con la capa de datos. La vista estará encargada de mostrar la información al usuario en los fragmentos. 

**•	Capa de dominio:** Esta capa contendrá las reglas de negocio y la lógica de aplicación. Las entidades y las interfaces de los repositorios estarán en esta capa.

**• Capa de datos:** Esta capa será la encargada de manejar la persistencia de los datos y la comunicación con los servicios externos, a través de la implementación de los repositorios y las fuentes de datos, que en este caso serán dos, la comunicación de la API REST y el cache que se guardara en BD local.




<p align="center">
  <a href="https://developer.android.com/topic/architecture?hl=es-419#fetching_data"><img src="https://github.com/diegulog/intellifit/blob/master/mvvm.png?raw=true" alt="MVVM"></a>
</p>


## Prototipo horizontal de alta fidelidad 

https://www.figma.com/proto/lC8z8321CqZDh4YgVTmJ2D/IntelliFit?page-id=53798%3A27459


## Demo

![](https://github.com/diegulog/intellifit/blob/master/demo.gif?raw=true)


## Probar la aplicación

Todas las llamadas a la API estan simuladas, necesita ejecutar la variante de compilacion DEMO.

## License

    Copyright 2020 Diego Guaña

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.