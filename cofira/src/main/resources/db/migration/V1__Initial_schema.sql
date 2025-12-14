-- ========================================
-- COFIRA - Initial Database Schema
-- Version: 1.0
-- Description: Schema inicial con todas las tablas del sistema
-- ========================================

-- ========================================
-- TABLA: usuarios
-- ========================================
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    edad INTEGER,
    peso DOUBLE PRECISION,
    altura DOUBLE PRECISION,
    rutina_alimentacion_id BIGINT,
    rutina_ejercicio_id BIGINT,
    CONSTRAINT check_rol CHECK (rol IN ('USER', 'ADMIN'))
);

CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_usuarios_email ON usuarios(email);

-- ========================================
-- TABLA: token_revocado
-- ========================================
CREATE TABLE IF NOT EXISTS revoked_tokens (
    id BIGSERIAL PRIMARY KEY,
    jti VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_revoked_tokens_jti ON revoked_tokens(jti);
CREATE INDEX idx_revoked_tokens_expires_at ON revoked_tokens(expires_at);

-- ========================================
-- TABLA: objetivos
-- ========================================
CREATE TABLE IF NOT EXISTS objetivos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_objetivos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- ========================================
-- TABLA: lista_objetivos
-- ========================================
CREATE TABLE IF NOT EXISTS lista_objetivos (
    objetivos_id BIGINT NOT NULL,
    objetivo VARCHAR(255),
    CONSTRAINT fk_lista_objetivos FOREIGN KEY (objetivos_id) REFERENCES objetivos(id) ON DELETE CASCADE
);

-- ========================================
-- TABLA: planes
-- ========================================
CREATE TABLE IF NOT EXISTS plan (
    id BIGSERIAL PRIMARY KEY,
    precio DOUBLE PRECISION NOT NULL,
    subscripcion_activa BOOLEAN NOT NULL,
    usuario_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_plan_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- ========================================
-- TABLA: salas_de_gimnasio
-- ========================================
CREATE TABLE IF NOT EXISTS sala_de_gimnasio (
    id BIGSERIAL PRIMARY KEY,
    fecha_fin DATE,
    fecha_inicio DATE
);

-- ========================================
-- TABLA: ejercicios
-- ========================================
CREATE TABLE IF NOT EXISTS ejercicios (
    id BIGSERIAL PRIMARY KEY,
    nombre_ejercicio VARCHAR(255) NOT NULL,
    series INTEGER NOT NULL,
    repeticiones INTEGER NOT NULL,
    tiempo_descanso_segundos INTEGER,
    descripcion TEXT,
    grupo_muscular VARCHAR(255)
);

CREATE INDEX idx_ejercicios_nombre ON ejercicios(nombre_ejercicio);
CREATE INDEX idx_ejercicios_grupo_muscular ON ejercicios(grupo_muscular);

-- ========================================
-- TABLA: alimentos
-- ========================================
CREATE TABLE IF NOT EXISTS alimento (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL
);

CREATE INDEX idx_alimento_nombre ON alimento(nombre);

-- ========================================
-- TABLA: ingredientes_alimento
-- ========================================
CREATE TABLE IF NOT EXISTS ingredientes_alimento (
    alimento_id BIGINT NOT NULL,
    ingrediente VARCHAR(255),
    CONSTRAINT fk_ingredientes_alimento FOREIGN KEY (alimento_id) REFERENCES alimento(id) ON DELETE CASCADE
);

-- ========================================
-- TABLA: rutinas_alimentacion
-- ========================================
CREATE TABLE IF NOT EXISTS rutina_alimentacion (
    id BIGSERIAL PRIMARY KEY,
    fecha_inicio DATE NOT NULL
);

-- ========================================
-- TABLA: rutinas_ejercicio
-- ========================================
CREATE TABLE IF NOT EXISTS rutina_ejercicio (
    id BIGSERIAL PRIMARY KEY,
    fecha_inicio DATE NOT NULL
);

-- ========================================
-- TABLA: dias_alimentacion
-- ========================================
CREATE TABLE IF NOT EXISTS dia_alimentacion (
    id BIGSERIAL PRIMARY KEY,
    dia_semana VARCHAR(50) NOT NULL,
    desayuno_id BIGINT,
    almuerzo_id BIGINT,
    comida_id BIGINT,
    merienda_id BIGINT,
    cena_id BIGINT,
    rutina_alimentacion_id BIGINT,
    CONSTRAINT check_dia_semana CHECK (dia_semana IN ('LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO')),
    CONSTRAINT fk_dia_alimentacion_rutina FOREIGN KEY (rutina_alimentacion_id) REFERENCES rutina_alimentacion(id) ON DELETE CASCADE
);

-- ========================================
-- TABLA: desayuno
-- ========================================
CREATE TABLE IF NOT EXISTS desayuno (
    id BIGSERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS desayuno_alimentos (
    desayuno_id BIGINT NOT NULL,
    alimento VARCHAR(255),
    CONSTRAINT fk_desayuno_alimentos FOREIGN KEY (desayuno_id) REFERENCES desayuno(id) ON DELETE CASCADE
);

-- ========================================
-- TABLA: almuerzo
-- ========================================
CREATE TABLE IF NOT EXISTS almuerzo (
    id BIGSERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS almuerzo_alimentos (
    almuerzo_id BIGINT NOT NULL,
    alimento VARCHAR(255),
    CONSTRAINT fk_almuerzo_alimentos FOREIGN KEY (almuerzo_id) REFERENCES almuerzo(id) ON DELETE CASCADE
);

-- ========================================
-- TABLA: comida
-- ========================================
CREATE TABLE IF NOT EXISTS comida (
    id BIGSERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS comida_alimentos (
    comida_id BIGINT NOT NULL,
    alimento VARCHAR(255),
    CONSTRAINT fk_comida_alimentos FOREIGN KEY (comida_id) REFERENCES comida(id) ON DELETE CASCADE
);

-- ========================================
-- TABLA: merienda
-- ========================================
CREATE TABLE IF NOT EXISTS merienda (
    id BIGSERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS merienda_alimentos (
    merienda_id BIGINT NOT NULL,
    alimento VARCHAR(255),
    CONSTRAINT fk_merienda_alimentos FOREIGN KEY (merienda_id) REFERENCES merienda(id) ON DELETE CASCADE
);

-- ========================================
-- TABLA: cena
-- ========================================
CREATE TABLE IF NOT EXISTS cena (
    id BIGSERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS cena_alimentos (
    cena_id BIGINT NOT NULL,
    alimento VARCHAR(255),
    CONSTRAINT fk_cena_alimentos FOREIGN KEY (cena_id) REFERENCES cena(id) ON DELETE CASCADE
);

-- ========================================
-- TABLA: dias_ejercicio
-- ========================================
CREATE TABLE IF NOT EXISTS dia_ejercicio (
    id BIGSERIAL PRIMARY KEY,
    dia_semana VARCHAR(50) NOT NULL,
    rutina_ejercicio_id BIGINT,
    CONSTRAINT check_dia_ejercicio_semana CHECK (dia_semana IN ('LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO')),
    CONSTRAINT fk_dia_ejercicio_rutina FOREIGN KEY (rutina_ejercicio_id) REFERENCES rutina_ejercicio(id) ON DELETE CASCADE
);

-- ========================================
-- TABLA: dia_ejercicio_ejercicios (ManyToMany)
-- ========================================
CREATE TABLE IF NOT EXISTS dia_ejercicio_ejercicios (
    dia_ejercicio_id BIGINT NOT NULL,
    ejercicio_id BIGINT NOT NULL,
    PRIMARY KEY (dia_ejercicio_id, ejercicio_id),
    CONSTRAINT fk_dia_ejercicio FOREIGN KEY (dia_ejercicio_id) REFERENCES dia_ejercicio(id) ON DELETE CASCADE,
    CONSTRAINT fk_ejercicio FOREIGN KEY (ejercicio_id) REFERENCES ejercicios(id) ON DELETE CASCADE
);

-- ========================================
-- TABLA: usuario_alimentos_favoritos
-- ========================================
CREATE TABLE IF NOT EXISTS usuario_alimentos_favoritos (
    usuario_id BIGINT NOT NULL,
    alimento_favorito VARCHAR(255),
    CONSTRAINT fk_usuario_alimentos_favoritos FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- ========================================
-- TABLA: usuario_alergias
-- ========================================
CREATE TABLE IF NOT EXISTS usuario_alergias (
    usuario_id BIGINT NOT NULL,
    alergia VARCHAR(255),
    CONSTRAINT fk_usuario_alergias FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- ========================================
-- FOREIGN KEYS ADICIONALES (Referencia circular)
-- ========================================
ALTER TABLE usuarios
    ADD CONSTRAINT fk_usuarios_rutina_alimentacion
    FOREIGN KEY (rutina_alimentacion_id) REFERENCES rutina_alimentacion(id) ON DELETE SET NULL;

ALTER TABLE usuarios
    ADD CONSTRAINT fk_usuarios_rutina_ejercicio
    FOREIGN KEY (rutina_ejercicio_id) REFERENCES rutina_ejercicio(id) ON DELETE SET NULL;

ALTER TABLE dia_alimentacion
    ADD CONSTRAINT fk_dia_alimentacion_desayuno
    FOREIGN KEY (desayuno_id) REFERENCES desayuno(id) ON DELETE CASCADE;

ALTER TABLE dia_alimentacion
    ADD CONSTRAINT fk_dia_alimentacion_almuerzo
    FOREIGN KEY (almuerzo_id) REFERENCES almuerzo(id) ON DELETE CASCADE;

ALTER TABLE dia_alimentacion
    ADD CONSTRAINT fk_dia_alimentacion_comida
    FOREIGN KEY (comida_id) REFERENCES comida(id) ON DELETE CASCADE;

ALTER TABLE dia_alimentacion
    ADD CONSTRAINT fk_dia_alimentacion_merienda
    FOREIGN KEY (merienda_id) REFERENCES merienda(id) ON DELETE CASCADE;

ALTER TABLE dia_alimentacion
    ADD CONSTRAINT fk_dia_alimentacion_cena
    FOREIGN KEY (cena_id) REFERENCES cena(id) ON DELETE CASCADE;

-- ========================================
-- COMENTARIOS DE DOCUMENTACIÓN
-- ========================================
COMMENT ON TABLE usuarios IS 'Tabla principal de usuarios del sistema';
COMMENT ON TABLE revoked_tokens IS 'Blacklist de tokens JWT revocados';
COMMENT ON TABLE objetivos IS 'Objetivos personalizados de los usuarios';
COMMENT ON TABLE plan IS 'Planes de suscripción de los usuarios';
COMMENT ON TABLE sala_de_gimnasio IS 'Salas disponibles en el gimnasio';
COMMENT ON TABLE ejercicios IS 'Catálogo de ejercicios disponibles';
COMMENT ON TABLE alimento IS 'Catálogo de alimentos';
COMMENT ON TABLE rutina_alimentacion IS 'Rutinas de alimentación semanal';
COMMENT ON TABLE rutina_ejercicio IS 'Rutinas de ejercicio semanal';
COMMENT ON TABLE dia_alimentacion IS 'Días de la rutina de alimentación';
COMMENT ON TABLE dia_ejercicio IS 'Días de la rutina de ejercicio';
