USE biblioteca;

CREATE TABLE `libros` (
  `id` int NOT NULL,
  `codigo` varchar(6) NOT NULL,
  `titulo` varchar(45) NOT NULL,
  `autor` varchar(45) NOT NULL,
  `genero` varchar(45) NOT NULL,
  `editorial` varchar(45) NOT NULL,
  `disponible` tinyint DEFAULT '0',
  `publicacion` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo_UNIQUE` (`codigo`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `socios` (
  `id` int NOT NULL,
  `dni` varchar(45) NOT NULL,
  `nombre` varchar(45) NOT NULL,
  `apellidos` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `telefono` varchar(15) NOT NULL,
  `direccion` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `telefono_UNIQUE` (`telefono`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `usuarios` (
  `id` int NOT NULL,
  `nombre` varchar(45) NOT NULL,
  `password` int NOT NULL,
  `rol` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre_UNIQUE` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `usuarios` (`id`, `nombre`, `password`, `rol`) VALUES
(0, 'admin', 92668751, 'Administrador');

CREATE TABLE `prestamos` (
  `id` int NOT NULL,
  `idSocio` int NOT NULL,
  `codLibro` varchar(10) NOT NULL,
  `fechaPrestamo` date NOT NULL,
  `fechaLimite` date NOT NULL,
  `devolucion` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
