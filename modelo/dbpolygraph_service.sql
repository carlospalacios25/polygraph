-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: bdpolyservice
-- ------------------------------------------------------
-- Server version	8.4.3

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `analisis`
--

DROP TABLE IF EXISTS `analisis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `analisis` (
  `Id_Analisis` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del análisis.',
  `Id_Servicio` int NOT NULL COMMENT 'Servicio asociado al análisis.',
  `Tipo_Analisis` enum('Visita','Final') NOT NULL COMMENT 'Tipo de análisis (Visita o Final).',
  `Contenido` varchar(300) NOT NULL COMMENT 'Contenido del análisis.',
  PRIMARY KEY (`Id_Analisis`),
  KEY `Id_Servicio` (`Id_Servicio`),
  CONSTRAINT `analisis_ibfk_1` FOREIGN KEY (`Id_Servicio`) REFERENCES `servicios` (`Id_Servicio`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para almacenar análisis de los servicios.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `analisis`
--

LOCK TABLES `analisis` WRITE;
/*!40000 ALTER TABLE `analisis` DISABLE KEYS */;
INSERT INTO `analisis` VALUES (1,1,'Visita','El candidato mostró buena disposición durante la visita.'),(2,1,'Final','Resultados consistentes con la evaluación inicial.'),(3,2,'Visita','Visita realizada sin inconvenientes.'),(4,2,'Final','Aprobado tras revisión de antecedentes.'),(5,3,'Visita','Visita inicial completada con observaciones positivas.'),(6,3,'Final','Pendiente de revisión final.');
/*!40000 ALTER TABLE `analisis` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `candidatos`
--

DROP TABLE IF EXISTS `candidatos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `candidatos` (
  `Cedula_Candidato` bigint NOT NULL COMMENT 'Cédula o identificación única del candidato.',
  `Nombre_Candidato` varchar(60) NOT NULL COMMENT 'Nombres completos del candidato.',
  `Apellido_Candidato` varchar(60) NOT NULL COMMENT 'Apellidos completos del candidato.',
  `Telefono_Candidato` varchar(15) NOT NULL COMMENT 'Número de teléfono del candidato (formato internacional, ej. +573001234567).',
  `Direccion_Candidato` varchar(100) NOT NULL COMMENT 'Dirección residencial del candidato.',
  `Id_Ciudad` int NOT NULL COMMENT 'Ciudad de residencia del candidato.',
  PRIMARY KEY (`Cedula_Candidato`),
  KEY `Id_Ciudad` (`Id_Ciudad`),
  CONSTRAINT `candidatos_ibfk_1` FOREIGN KEY (`Id_Ciudad`) REFERENCES `ciudades` (`Id_Ciudad`) ON DELETE RESTRICT,
  CONSTRAINT `chk_telefono_candidato` CHECK (regexp_like(`Telefono_Candidato`,_utf8mb4'^+[0-9]{10,15}$'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para almacenar información de los candidatos.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `candidatos`
--

LOCK TABLES `candidatos` WRITE;
/*!40000 ALTER TABLE `candidatos` DISABLE KEYS */;
/*!40000 ALTER TABLE `candidatos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ciudades`
--

DROP TABLE IF EXISTS `ciudades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ciudades` (
  `Id_Ciudad` int NOT NULL AUTO_INCREMENT,
  `Nombre_Ciudad` varchar(100) NOT NULL COMMENT 'Nombre de la ciudad (ej. Bogotá, Medellín).',
  PRIMARY KEY (`Id_Ciudad`),
  UNIQUE KEY `Nombre_Ciudad` (`Nombre_Ciudad`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para almacenar ciudades de los servicios.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ciudades`
--

LOCK TABLES `ciudades` WRITE;
/*!40000 ALTER TABLE `ciudades` DISABLE KEYS */;
INSERT INTO `ciudades` VALUES (1,'Bogotá'),(3,'Cali'),(2,'Medellín');
/*!40000 ALTER TABLE `ciudades` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `Nit_Cliente` bigint NOT NULL COMMENT 'NIT o identificación única del cliente.',
  `Nombre_Cliente` varchar(60) NOT NULL COMMENT 'Nombre o razón social del cliente.',
  `Telefono_Cliente` varchar(15) NOT NULL COMMENT 'Número de teléfono del cliente (formato internacional).',
  `Direccion_Cliente` varchar(100) NOT NULL COMMENT 'Dirección del cliente.',
  `Id_Ciudad` int NOT NULL COMMENT 'Ciudad donde está ubicado el cliente.',
  PRIMARY KEY (`Nit_Cliente`),
  KEY `Id_Ciudad` (`Id_Ciudad`),
  CONSTRAINT `clientes_ibfk_1` FOREIGN KEY (`Id_Ciudad`) REFERENCES `ciudades` (`Id_Ciudad`) ON DELETE RESTRICT,
  CONSTRAINT `chk_telefono_cliente` CHECK (regexp_like(`Telefono_Cliente`,_utf8mb4'^+[0-9]{10,15}$'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para almacenar información de los clientes.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `documentos`
--

DROP TABLE IF EXISTS `documentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `documentos` (
  `Id_Documento` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del documento.',
  `Id_Servicio` int NOT NULL COMMENT 'Servicio asociado al documento.',
  `Tipo_Documento` enum('Informe Poligrafía','Reporte Visita','Certificado Antecedentes','Otro') NOT NULL COMMENT 'Tipo de documento.',
  `Nombre_Archivo` varchar(150) NOT NULL COMMENT 'Nombre del archivo del documento, con longitud aumentada para nombres más largos.',
  `Fecha_Carga` datetime NOT NULL COMMENT 'Fecha y hora de carga del documento, ahora con precisión de hora.',
  `Descripcion` text COMMENT 'Descripción opcional del documento, ahora con tipo text para mayor capacidad.',
  `Tamaño_Archivo` bigint DEFAULT NULL COMMENT 'Tamaño del archivo en bytes, nuevo campo para control de almacenamiento.',
  `Estado_Documento` enum('Activo','Archivado','Eliminado') NOT NULL DEFAULT 'Activo' COMMENT 'Estado del documento (Activo, Archivado o Eliminado).',
  `Fecha_Solicitud` date DEFAULT NULL COMMENT 'Fecha de solicitud del documento.',
  `Fecha_Recibido` date DEFAULT NULL COMMENT 'Fecha de recepción del documento.',
  `Habes_Data` varchar(255) DEFAULT NULL COMMENT 'Datos relacionados con Habes, opcional.',
  `Comunicados` text COMMENT 'Comunicados o notas asociadas al documento.',
  PRIMARY KEY (`Id_Documento`),
  KEY `Id_Servicio` (`Id_Servicio`),
  KEY `idx_fecha_carga` (`Fecha_Carga`),
  CONSTRAINT `documentos_ibfk_1` FOREIGN KEY (`Id_Servicio`) REFERENCES `servicios` (`Id_Servicio`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para almacenar documentos asociados a servicios con campos actualizados.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `documentos`
--

LOCK TABLES `documentos` WRITE;
/*!40000 ALTER TABLE `documentos` DISABLE KEYS */;
INSERT INTO `documentos` VALUES (1,1,'Informe Poligrafía','informe_poligrafia_001.pdf','2025-10-03 14:30:00','Informe de la prueba de poligrafía para Juan Pérez.',102400,'Activo',NULL,NULL,NULL,NULL),(2,1,'Reporte Visita','reporte_visita_001.pdf','2025-10-03 15:00:00','Reporte de la visita inicial.',51200,'Activo',NULL,NULL,NULL,NULL),(3,2,'Informe Poligrafía','informe_poligrafia_002.pdf','2025-10-04 09:15:00','Informe de la prueba de poligrafía para María Gómez.',153600,'Activo',NULL,NULL,NULL,NULL),(4,2,'Certificado Antecedentes','certificado_antecedentes_002.pdf','2025-10-04 10:00:00','Certificado de antecedentes penales.',25600,'Activo',NULL,NULL,NULL,NULL),(5,3,'Reporte Visita','reporte_visita_003.pdf','2025-10-05 11:30:00','Reporte de la visita inicial para Carlos Rodríguez.',64000,'Activo',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `documentos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `perfiles`
--

DROP TABLE IF EXISTS `perfiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `perfiles` (
  `Id_Perfil` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del perfil.',
  `Nombre_Perfil` varchar(50) NOT NULL COMMENT 'Nombre del perfil (ej. Administrador, Analista).',
  `Descripcion` varchar(255) DEFAULT NULL COMMENT 'Descripción del perfil y sus funciones.',
  PRIMARY KEY (`Id_Perfil`),
  UNIQUE KEY `Nombre_Perfil` (`Nombre_Perfil`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para almacenar perfiles de usuario.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `perfiles`
--

LOCK TABLES `perfiles` WRITE;
/*!40000 ALTER TABLE `perfiles` DISABLE KEYS */;
INSERT INTO `perfiles` VALUES (1,'Administrador','Usuario con acceso completo al sistema.'),(2,'Analista','Usuario encargado de revisar y gestionar servicios.'),(3,'Poligrafista','Usuario encargado de realizar pruebas de poligrafía.');
/*!40000 ALTER TABLE `perfiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `perfiles_permisos`
--

DROP TABLE IF EXISTS `perfiles_permisos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `perfiles_permisos` (
  `Id_Perfil` int NOT NULL COMMENT 'Identificador del perfil.',
  `Id_Permiso` int NOT NULL COMMENT 'Identificador del permiso.',
  PRIMARY KEY (`Id_Perfil`,`Id_Permiso`),
  KEY `Id_Permiso` (`Id_Permiso`),
  CONSTRAINT `perfiles_permisos_ibfk_1` FOREIGN KEY (`Id_Perfil`) REFERENCES `perfiles` (`Id_Perfil`) ON DELETE CASCADE,
  CONSTRAINT `perfiles_permisos_ibfk_2` FOREIGN KEY (`Id_Permiso`) REFERENCES `permisos` (`Id_Permiso`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para asociar perfiles con permisos.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `perfiles_permisos`
--

LOCK TABLES `perfiles_permisos` WRITE;
/*!40000 ALTER TABLE `perfiles_permisos` DISABLE KEYS */;
INSERT INTO `perfiles_permisos` VALUES (1,1),(1,2),(2,2),(1,3),(2,3),(3,3);
/*!40000 ALTER TABLE `perfiles_permisos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permisos`
--

DROP TABLE IF EXISTS `permisos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permisos` (
  `Id_Permiso` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del permiso.',
  `Nombre_Permiso` varchar(50) NOT NULL COMMENT 'Nombre del permiso (ej. Crear_Servicio).',
  `Descripcion_Permiso` varchar(255) DEFAULT NULL COMMENT 'Descripción del permiso.',
  PRIMARY KEY (`Id_Permiso`),
  UNIQUE KEY `Nombre_Permiso` (`Nombre_Permiso`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para almacenar permisos del sistema.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permisos`
--

LOCK TABLES `permisos` WRITE;
/*!40000 ALTER TABLE `permisos` DISABLE KEYS */;
INSERT INTO `permisos` VALUES (1,'Crear_Servicio','Permite crear nuevos servicios.'),(2,'Editar_Servicio','Permite editar servicios existentes.'),(3,'Ver_Informes','Permite visualizar informes de servicios.');
/*!40000 ALTER TABLE `permisos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `poligrafias`
--

DROP TABLE IF EXISTS `poligrafias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `poligrafias` (
  `Id_Poligrafia` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador único de la prueba de poligrafía.',
  `Id_Servicio` int NOT NULL COMMENT 'Servicio asociado a la poligrafía.',
  `Id_Poligrafista` int NOT NULL COMMENT 'Poligrafista asignado a la prueba.',
  `Fecha_Asignacion` date NOT NULL COMMENT 'Fecha de asignación de la prueba.',
  `Asistencia` enum('Si','No') NOT NULL COMMENT 'Indica si el candidato asistió a la prueba.',
  `Fecha_Entrega` date NOT NULL COMMENT 'Fecha de entrega del informe de la prueba.',
  PRIMARY KEY (`Id_Poligrafia`),
  KEY `Id_Servicio` (`Id_Servicio`),
  KEY `Id_Poligrafista` (`Id_Poligrafista`),
  KEY `idx_fecha_asignacion` (`Fecha_Asignacion`),
  CONSTRAINT `poligrafias_ibfk_1` FOREIGN KEY (`Id_Servicio`) REFERENCES `servicios` (`Id_Servicio`) ON DELETE CASCADE,
  CONSTRAINT `poligrafias_ibfk_2` FOREIGN KEY (`Id_Poligrafista`) REFERENCES `poligrafistas` (`Id_Poligrafista`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para almacenar pruebas de poligrafía.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `poligrafias`
--

LOCK TABLES `poligrafias` WRITE;
/*!40000 ALTER TABLE `poligrafias` DISABLE KEYS */;
INSERT INTO `poligrafias` VALUES (1,1,1,'2025-10-01','Si','2025-10-03'),(2,2,2,'2025-10-02','No','2025-10-04'),(3,3,1,'2025-10-03','Si','2025-10-05');
/*!40000 ALTER TABLE `poligrafias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `poligrafistas`
--

DROP TABLE IF EXISTS `poligrafistas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `poligrafistas` (
  `Id_Poligrafista` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del poligrafista.',
  `Nombre_Poligrafista` varchar(60) NOT NULL COMMENT 'Nombre completo del poligrafista.',
  `Sala_Encargada` varchar(60) NOT NULL COMMENT 'Sala asignada al poligrafista.',
  PRIMARY KEY (`Id_Poligrafista`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para almacenar información de los poligrafistas.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `poligrafistas`
--

LOCK TABLES `poligrafistas` WRITE;
/*!40000 ALTER TABLE `poligrafistas` DISABLE KEYS */;
INSERT INTO `poligrafistas` VALUES (1,'Ana López','Sala 1'),(2,'Carlos Martínez','Sala 2');
/*!40000 ALTER TABLE `poligrafistas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proceso_tipos_progreso`
--

DROP TABLE IF EXISTS `proceso_tipos_progreso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proceso_tipos_progreso` (
  `Id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador único de la relación.',
  `Id_Proceso` int NOT NULL COMMENT 'Proceso asociado.',
  `Id_Tipo_Progreso` int NOT NULL COMMENT 'Tipo de progreso asociado.',
  `Habilitado` tinyint(1) DEFAULT '1' COMMENT 'Indica si el tipo de progreso está habilitado (1) o no (0).',
  PRIMARY KEY (`Id`),
  KEY `Id_Proceso` (`Id_Proceso`),
  KEY `Id_Tipo_Progreso` (`Id_Tipo_Progreso`),
  CONSTRAINT `proceso_tipos_progreso_ibfk_1` FOREIGN KEY (`Id_Proceso`) REFERENCES `procesos` (`Id_Proceso`) ON DELETE CASCADE,
  CONSTRAINT `proceso_tipos_progreso_ibfk_2` FOREIGN KEY (`Id_Tipo_Progreso`) REFERENCES `tipos_progreso` (`Id_Tipo_Progreso`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para asociar procesos con tipos de progreso.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proceso_tipos_progreso`
--

LOCK TABLES `proceso_tipos_progreso` WRITE;
/*!40000 ALTER TABLE `proceso_tipos_progreso` DISABLE KEYS */;
INSERT INTO `proceso_tipos_progreso` VALUES (1,1,1,1),(2,1,2,1),(3,2,3,1),(4,3,1,0),(5,1,3,1);
/*!40000 ALTER TABLE `proceso_tipos_progreso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `procesos`
--

DROP TABLE IF EXISTS `procesos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `procesos` (
  `Id_Proceso` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del proceso.',
  `Nombre_Proceso` varchar(100) NOT NULL COMMENT 'Nombre del proceso (ej. Selección de Personal).',
  PRIMARY KEY (`Id_Proceso`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para almacenar tipos de procesos.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `procesos`
--

LOCK TABLES `procesos` WRITE;
/*!40000 ALTER TABLE `procesos` DISABLE KEYS */;
INSERT INTO `procesos` VALUES (1,'Selección de Personal'),(2,'Verificación de Antecedentes'),(3,'Evaluación de Seguridad');
/*!40000 ALTER TABLE `procesos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `progreso`
--

DROP TABLE IF EXISTS `progreso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `progreso` (
  `Id_Progreso` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del progreso.',
  `Id_Tipo_Progr` int NOT NULL COMMENT 'Tipo de progreso asociado.',
  `Fecha_Progr` date NOT NULL COMMENT 'Fecha del progreso.',
  `Observacion_Ante` varchar(255) DEFAULT NULL COMMENT 'Observación anterior o de antecedentes.',
  `Id_Servicio` int NOT NULL COMMENT 'Servicio asociado al progreso.',
  `Nombre_usuario` varchar(50) NOT NULL COMMENT 'Nombre de usuario que registró el progreso.',
  PRIMARY KEY (`Id_Progreso`),
  KEY `Id_Tipo_Progr` (`Id_Tipo_Progr`),
  KEY `Id_Servicio` (`Id_Servicio`),
  KEY `idx_nombre_usuario` (`Nombre_usuario`),
  CONSTRAINT `progreso_ibfk_1` FOREIGN KEY (`Id_Tipo_Progr`) REFERENCES `tipos_progreso` (`Id_Tipo_Progreso`) ON DELETE RESTRICT,
  CONSTRAINT `progreso_ibfk_2` FOREIGN KEY (`Id_Servicio`) REFERENCES `servicios` (`Id_Servicio`) ON DELETE CASCADE,
  CONSTRAINT `progreso_ibfk_3` FOREIGN KEY (`Nombre_usuario`) REFERENCES `usuarios` (`Nombre_usuario`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para almacenar el progreso de los servicios.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `progreso`
--

LOCK TABLES `progreso` WRITE;
/*!40000 ALTER TABLE `progreso` DISABLE KEYS */;
INSERT INTO `progreso` VALUES (1,1,'2025-10-03','Observación inicial para poligrafía',1,'admin'),(2,2,'2025-10-03','Observación para visita',1,'analista1'),(3,3,'2025-10-03','Pendiente de antecedentes',1,'admin'),(4,1,'2025-10-04','No asistió a poligrafía',2,'poligrafista1'),(5,2,'2025-10-04','Visita completada',2,'analista1'),(6,3,'2025-10-04','Antecedentes verificados',2,'admin'),(7,1,'2025-10-05','Poligrafía realizada',3,'poligrafista1'),(8,2,'2025-10-05','Visita inicial completada',3,'analista1'),(9,3,'2025-10-05','Pendiente de verificación de antecedentes',3,'admin');
/*!40000 ALTER TABLE `progreso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servicios`
--

DROP TABLE IF EXISTS `servicios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servicios` (
  `Id_Servicio` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del servicio.',
  `Fecha_Solicitud` date NOT NULL COMMENT 'Fecha de solicitud del servicio.',
  `Hora_Solicitud` time NOT NULL COMMENT 'Hora de solicitud del servicio.',
  `Nit_Cliente` bigint NOT NULL COMMENT 'Cliente que solicita el servicio.',
  `Cedula_Candidato` bigint NOT NULL COMMENT 'Candidato asociado al servicio.',
  `Id_Proceso` int NOT NULL COMMENT 'Proceso asociado al servicio.',
  `Estado` enum('Pendiente','En Progreso','Completado','Cancelado') NOT NULL DEFAULT 'Pendiente' COMMENT 'Estado del servicio.',
  `Resultado` enum('Aprobado','No Aprobado','Pendiente') NOT NULL DEFAULT 'Pendiente' COMMENT 'Resultado del servicio.',
  `Facturacion_Servicio` varchar(60) DEFAULT NULL COMMENT 'Información de facturación del servicio.',
  `Verificacion_Servicio` varchar(100) DEFAULT NULL COMMENT 'Información de verificación del servicio.',
  `Cargo_Autofinanciera` varchar(100) DEFAULT NULL COMMENT 'Cargo relacionado con la autofinanciera.',
  `Autofinanciera` varchar(100) DEFAULT NULL COMMENT 'Entidad autofinanciera asociada.',
  `Empresas_Servicio` varchar(100) DEFAULT NULL COMMENT 'Empresas relacionadas con el servicio.',
  `Id_Sucursal` int DEFAULT NULL COMMENT 'Sucursal asociada al servicio.',
  `Centro_Costo` varchar(100) DEFAULT NULL COMMENT 'Centro de costo asociado.',
  `Fecha_Entrega_Estudio` date DEFAULT NULL COMMENT 'Fecha de entrega del estudio.',
  `Fecha_De_Envio` date DEFAULT NULL COMMENT 'Fecha de envio.',
  PRIMARY KEY (`Id_Servicio`),
  KEY `Nit_Cliente` (`Nit_Cliente`),
  KEY `Cedula_Candidato` (`Cedula_Candidato`),
  KEY `Id_Proceso` (`Id_Proceso`),
  KEY `Id_Sucursal` (`Id_Sucursal`),
  KEY `idx_fecha_solicitud` (`Fecha_Solicitud`),
  KEY `idx_estado` (`Estado`),
  CONSTRAINT `servicios_ibfk_1` FOREIGN KEY (`Nit_Cliente`) REFERENCES `clientes` (`Nit_Cliente`) ON DELETE RESTRICT,
  CONSTRAINT `servicios_ibfk_2` FOREIGN KEY (`Cedula_Candidato`) REFERENCES `candidatos` (`Cedula_Candidato`) ON DELETE RESTRICT,
  CONSTRAINT `servicios_ibfk_3` FOREIGN KEY (`Id_Proceso`) REFERENCES `procesos` (`Id_Proceso`) ON DELETE RESTRICT,
  CONSTRAINT `servicios_ibfk_4` FOREIGN KEY (`Id_Sucursal`) REFERENCES `sucursales` (`Id_Sucursal`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para almacenar servicios solicitados.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servicios`
--

LOCK TABLES `servicios` WRITE;
/*!40000 ALTER TABLE `servicios` DISABLE KEYS */;
INSERT INTO `servicios` VALUES (1,'2025-10-01','09:00:00',900123456,1234567890,1,'En Progreso','Pendiente','Factura 001','Verificado','Analista','Autofinanciera XYZ','Empresa XYZ',1,'CC-001',NULL,NULL),(2,'2025-10-02','10:30:00',900654321,9876543210,2,'Completado','Aprobado','Factura 002','Verificado','Gerente','Autofinanciera ABC','Compañía ABC',2,'CC-002',NULL,NULL),(3,'2025-10-03','11:00:00',900123456,4567891234,3,'Pendiente','Pendiente','Factura 003','Pendiente','Supervisor','Autofinanciera XYZ','Empresa XYZ',3,'CC-003',NULL,NULL);
/*!40000 ALTER TABLE `servicios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sucursales`
--

DROP TABLE IF EXISTS `sucursales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sucursales` (
  `Id_Sucursal` int NOT NULL AUTO_INCREMENT,
  `Nombre_Sucursal` varchar(100) NOT NULL COMMENT 'Nombre de la sucursal (ej. Sucursal Norte).',
  `Id_Ciudad` int NOT NULL COMMENT 'Ciudad donde está ubicada la sucursal.',
  PRIMARY KEY (`Id_Sucursal`),
  KEY `Id_Ciudad` (`Id_Ciudad`),
  CONSTRAINT `sucursales_ibfk_1` FOREIGN KEY (`Id_Ciudad`) REFERENCES `ciudades` (`Id_Ciudad`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para almacenar sucursales de los clientes.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sucursales`
--

LOCK TABLES `sucursales` WRITE;
/*!40000 ALTER TABLE `sucursales` DISABLE KEYS */;
INSERT INTO `sucursales` VALUES (1,'Sucursal Norte',1),(2,'Sucursal Centro',2),(3,'Sucursal Sur',3);
/*!40000 ALTER TABLE `sucursales` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipos_progreso`
--

DROP TABLE IF EXISTS `tipos_progreso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipos_progreso` (
  `Id_Tipo_Progreso` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del tipo de progreso.',
  `Nombre_Progreso` varchar(100) NOT NULL COMMENT 'Nombre del tipo de progreso (ej. Poligrafía, Visita).',
  PRIMARY KEY (`Id_Tipo_Progreso`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para almacenar tipos de progreso.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipos_progreso`
--

LOCK TABLES `tipos_progreso` WRITE;
/*!40000 ALTER TABLE `tipos_progreso` DISABLE KEYS */;
INSERT INTO `tipos_progreso` VALUES (1,'Poligrafía'),(2,'Visita'),(3,'Antecedente'),(4,'Financiero'),(5,'Académico'),(6,'Laboral'),(7,'Personal'),(8,'Pasta'),(9,'Sicotécnica');
/*!40000 ALTER TABLE `tipos_progreso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `Id_Usuario` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del usuario.',
  `Nombre_Emp1` varchar(100) DEFAULT NULL COMMENT 'Nombre del empleado.',
  `Apellido_Emp1` varchar(100) DEFAULT NULL COMMENT 'Apellido del empleado.',
  `Nombre_usuario` varchar(50) DEFAULT NULL COMMENT 'Nombre de usuario para inicio de sesión.',
  `Correo_Usu` varchar(100) DEFAULT NULL COMMENT 'Correo electrónico del usuario.',
  `Contraseña_Usu` varchar(255) DEFAULT NULL COMMENT 'Contraseña cifrada del usuario.',
  `Fecha_Creacion` date DEFAULT NULL COMMENT 'Fecha de creación del usuario.',
  `Activo_Usu` tinyint(1) DEFAULT NULL COMMENT 'Indica si el usuario está activo (1) o no (0).',
  `Id_perfil` int DEFAULT NULL COMMENT 'Perfil asignado al usuario.',
  PRIMARY KEY (`Id_Usuario`),
  UNIQUE KEY `Nombre_usuario` (`Nombre_usuario`),
  UNIQUE KEY `Correo_Usu` (`Correo_Usu`),
  KEY `Id_perfil` (`Id_perfil`),
  CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`Id_perfil`) REFERENCES `perfiles` (`Id_Perfil`) ON DELETE RESTRICT,
  CONSTRAINT `chk_correo_usu` CHECK (regexp_like(`Correo_Usu`,_utf8mb4'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$'))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para almacenar usuarios del sistema.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'Admin','Sistema','admin','admin@polygraph.com','hashed_password_123','2025-10-01',1,1),(2,'Analista','Uno','analista1','analista1@polygraph.com','hashed_password_456','2025-10-02',1,2),(3,'Poligrafista','Dos','poligrafista1','poligrafista1@polygraph.com','hashed_password_789','2025-10-03',1,3);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `visitadores`
--

DROP TABLE IF EXISTS `visitadores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `visitadores` (
  `Id_Visitador` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del visitador.',
  `Nombre_Visitador` varchar(60) NOT NULL COMMENT 'Nombre completo del visitador.',
  `Zonas_Visitador` varchar(60) NOT NULL COMMENT 'Zonas asignadas al visitador.',
  PRIMARY KEY (`Id_Visitador`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para almacenar información de los visitadores.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `visitadores`
--

LOCK TABLES `visitadores` WRITE;
/*!40000 ALTER TABLE `visitadores` DISABLE KEYS */;
INSERT INTO `visitadores` VALUES (1,'Carlos Gómez','Zona Norte'),(2,'Laura Sánchez','Zona Centro');
/*!40000 ALTER TABLE `visitadores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `visitas`
--

DROP TABLE IF EXISTS `visitas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `visitas` (
  `Id_Visita` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador único de la visita.',
  `Id_Servicio` int NOT NULL COMMENT 'Servicio asociado a la visita.',
  `Id_Visitador` int NOT NULL COMMENT 'Visitador asignado a la visita.',
  `Tipo_Prueba` enum('Virtual','Presencial') NOT NULL COMMENT 'Tipo de prueba (Virtual o Presencial).',
  `Tipo_Visita` enum('Inicial','Seguimiento') NOT NULL COMMENT 'Tipo de visita (Inicial o Seguimiento).',
  `Fecha_Solicitud` date NOT NULL COMMENT 'Fecha de solicitud de la visita.',
  `Fecha_Visita` date NOT NULL COMMENT 'Fecha en que se realiza la visita.',
  `Hora_Visita` time NOT NULL COMMENT 'Hora de la visita.',
  `Fecha_Envio_Informe` date NOT NULL COMMENT 'Fecha de envío del informe de la visita.',
  `Novedad_Visita` varchar(500) DEFAULT NULL COMMENT 'Observaciones o novedades de la visita.',
  PRIMARY KEY (`Id_Visita`),
  KEY `Id_Servicio` (`Id_Servicio`),
  KEY `Id_Visitador` (`Id_Visitador`),
  KEY `idx_fecha_visita` (`Fecha_Visita`),
  CONSTRAINT `visitas_ibfk_1` FOREIGN KEY (`Id_Servicio`) REFERENCES `servicios` (`Id_Servicio`) ON DELETE CASCADE,
  CONSTRAINT `visitas_ibfk_2` FOREIGN KEY (`Id_Visitador`) REFERENCES `visitadores` (`Id_Visitador`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para almacenar información de las visitas.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `visitas`
--

LOCK TABLES `visitas` WRITE;
/*!40000 ALTER TABLE `visitas` DISABLE KEYS */;
INSERT INTO `visitas` VALUES (1,1,1,'Virtual','Inicial','2025-10-01','2025-10-02','10:00:00','2025-10-03','Sin novedades'),(2,2,2,'Presencial','Seguimiento','2025-10-02','2025-10-03','14:30:00','2025-10-04','Candidato ausente'),(3,3,1,'Presencial','Inicial','2025-10-03','2025-10-04','09:00:00','2025-10-05','Visita realizada con éxito');
/*!40000 ALTER TABLE `visitas` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-06 22:18:42
