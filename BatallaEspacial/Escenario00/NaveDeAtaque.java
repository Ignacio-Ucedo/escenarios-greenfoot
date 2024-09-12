import greenfoot.*;

public class NaveDeAtaque extends NaveAliada implements Atacante {

    /**
     * Representa el estado de los motores de la {@link NaveDeAtaque}.
     */
    protected boolean motoresEncendidos = false;

    /*la nave empieza sin aura */
    protected int aura=-1;
    protected Direccion orientacion = Direccion.NORTE;
    /**
     * Inicializa una nueva NaveDeAtaque con los motores apagados
     */
    public NaveDeAtaque() {
        super();
    }

    /**
     * Inicializa una nueva NaveDeAtaque con los motores apagados. Este constructor
     * es empleado mayormente para la creación de escenarios.
     * 
     * @param direccion es la orientación con la que se creará la NaveDeAtaque
     * @param carga     es la carga de combustible inicial de la NaveDeAtaque
     */
    public NaveDeAtaque(Direccion direccion, int carga) {
        super();
        setDireccion(direccion);
        this.orientacion = direccion;
        this.combustible = carga;
    }

    /**
     * pre: posee combustible {@link NaveAliada#combustible} y los motores se
     * encuentran apagados {@link NaveDeAtaque#motoresEncendidos} <br/>
     * post: encenderá sus motores
     */
    public void encenderMotores() {
        if (this.combustible > 0 && !this.motoresEncendidos) {
            this.motoresEncendidos = true;
            Greenfoot.playSound("engine-on.wav");
            int tamCelda = getWorld().getCellSize();
            imagenBase = new GreenfootImage("weaponized-ship-on.png");
            imagenBase.scale((int) (tamCelda * ESCALA_X), (int) (tamCelda * ESCALA_Y));
            actualizarImagen();
        }
    }

    /**
     * pre: los motores se encuentran encendidos
     * {@link NaveDeAtaque#motoresEncendidos} <br/>
     * post: apagará sus motores
     */
    public void apagarMotores() {
        if (this.motoresEncendidos) {
            this.motoresEncendidos = false;
            Greenfoot.playSound("engine-off.wav");
            int tamCelda = getWorld().getCellSize();
            imagenBase = new GreenfootImage("weaponized-ship.png");
            imagenBase.scale((int) (tamCelda * ESCALA_X), (int) (tamCelda * ESCALA_Y));
            actualizarImagen();
        }
    }

    /**
     * {@inheritDoc}
     */
    protected boolean puedeActuar() {
        return super.puedeActuar() && this.motoresEncendidos;
    }

    /**
     * {@inheritDoc} <br>
     * post: si se agota el {@link NaveAliada#combustible}, se apagarán los motores
     */
    protected void consumirCombustible(int cantidad) {
        super.consumirCombustible(cantidad);
        if (combustible <= 0) {
            this.apagarMotores();
        }
    }

    /**
     * pre: La NaveDeAtaque {@link #puedeActuar()} <br>
     * post: El {@link NaveAliada#combustible} se reducirá en
     * {@link #obtenerConsumoPorAtaque()}. Si en la dirección deseada hay un
     * {@link Dañable}, éste recibirá {@link #obtenerDaño()}.
     * 
     * @param direccion
     */
    public void atacarHacia(Direccion direccion) {
        if (!puedeActuar()) {
            return;
        }
        this.direccion = direccion;
        actualizarImagen();
        setRotation(direccion.rotacion);
        Greenfoot.delay(20);
        consumirCombustible(obtenerConsumoPorAtaque());

        Actor actor = getOneObjectAtOffset(this.direccion.dx, this.direccion.dy, Actor.class);
        if (!(actor instanceof Dañable)) {
            return;
        }
        Dañable objetivo = (Dañable) actor;
        if (objetivo != null) {
            Greenfoot.playSound("laser-shot.wav");
            objetivo.recibirDañoDe(this);
        }
    }

    /**
     * @see NaveAliada#moverHacia(Direccion)
     */
    public void avanzarHacia(Direccion direccion) {
        Direccion orientacion_previa = this.orientacion;
        int x_previo = this.getX();
        int y_previo = this.getY();

        boolean se_movio = super.moverHacia(direccion);

        if(se_movio){
            this.orientacion = direccion;
            actualizarEstela(orientacion_previa, x_previo, y_previo);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int obtenerCombustible() {
        return super.obtenerCombustible();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int obtenerCombustibleMaximo() {
        return 150;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int obtenerDaño() {
        return 35;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int obtenerConsumoPorMovimiento() {
        return 7;
    }

    /**
     * @return la cantidad de combustible que consume realizar un ataque
     */
    int obtenerConsumoPorAtaque() {
        return 10;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean estaEnElBorde() {
        return super.estaEnElBorde();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hayVacioHacia(Direccion direccion) {
        return super.hayVacioHacia(direccion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hayAsteroideHacia(Direccion direccion) {
        return super.hayAsteroideHacia(direccion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hayItemHacia(Direccion direccion) {
        return super.hayItemHacia(direccion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hayNaveHacia(Direccion direccion) {
        return super.hayNaveHacia(direccion);
    }

    /**
     * pre: La NaveDeAtaque {@link #puedeActuar()} <br>
     * post: Obtiene la salud de una NaveDeAtaqueEnemiga o Asteroide, en
     * cierta Direccion.
     * 
     * @param direccion
     * @return la salud de una nave enemiga o el tamaño de un asteroide
     */
    public int escanearIndicadorHacia(Direccion direccion) {
        int valor = 0;
        if (hayNaveHacia(direccion)) {
            NaveDeAtaqueEnemiga nave = (NaveDeAtaqueEnemiga) getOneObjectAtOffset(direccion.dx, direccion.dy,
                    NaveDeAtaqueEnemiga.class);
            valor = nave.obtenerSalud();
        } else if (hayAsteroideHacia(direccion)) {
            Asteroide asteroide = (Asteroide) getOneObjectAtOffset(direccion.dx, direccion.dy, Asteroide.class);
            valor = asteroide.obtenerTamaño();
        }
        return valor;
    }

    private void actualizarEstela(Direccion orientacion_previa, int x_previo, int y_previo){
        MyGreenfootImage fragmento_estela = obtenerFragmentoEstela(orientacion_previa, this.orientacion);
        Color color_estela;
        if (aura >=  0){
            color_estela = MyGreenfootImage.AURAS[aura % MyGreenfootImage.AURAS.length];
        } else{
            color_estela = MyGreenfootImage.AURAS[0];
        }

        fragmento_estela.applyColor(color_estela);
        getWorld().getBackground().drawImage(fragmento_estela, x_previo * getWorld().getCellSize(), y_previo * getWorld().getCellSize());
    }

    private MyGreenfootImage obtenerFragmentoEstela(Direccion orientacion_previa, Direccion orientacion_actual) {
        int tam_celda = getWorld().getCellSize();
        GreenfootImage fragmento_estela = new GreenfootImage(tam_celda, tam_celda);
        int grosor_fragmento_estela = 4; 
        //coordenada x más pequeña del fragmento estela
        int inicio_x_fragmento = tam_celda/2 - grosor_fragmento_estela/2;        
        Color color_base_estela = new Color(255, 255, 255);
        int transparencia_estela = 70;
        fragmento_estela.setTransparency(transparencia_estela);
        int desface = tam_celda/10;

        // recta
        if (orientacion_previa == orientacion_actual) {
            for (int j = 0; j < tam_celda; j++) {
                for (int i = inicio_x_fragmento + desface; i < inicio_x_fragmento + grosor_fragmento_estela + desface; i++) {
                    fragmento_estela.setColorAt(i, j, color_base_estela);
                }
            }
        // vuelta en u
        } else if (orientacion_previa == orientacion_actual.opuesta()) {
            for (int j = 0; j < tam_celda; j++) {
                if(j==tam_celda/2){
                    fragmento_estela.rotate(180);
                }
                for (int i = inicio_x_fragmento + desface; i < inicio_x_fragmento + grosor_fragmento_estela + desface; i++) {
                    fragmento_estela.setColorAt(i, j, color_base_estela);
                }
            }
            for (int i = inicio_x_fragmento-desface; i < inicio_x_fragmento + desface + grosor_fragmento_estela; i++) {
                for(int j = tam_celda/2; j<tam_celda/2 + grosor_fragmento_estela; j++){
                    fragmento_estela.setColorAt(i, j, color_base_estela);

                }
            }
            fragmento_estela.rotate(180);

        // giro
        } else {
            if (this.direccionADerecha(orientacion_previa) == orientacion_actual) {
                desface = -desface;
            }
            for (int j = 0; j < tam_celda/2 + desface; j++) {
                for(int i = inicio_x_fragmento + desface; i< inicio_x_fragmento + desface + grosor_fragmento_estela; i++) {
                    fragmento_estela.setColorAt(i, j, color_base_estela);
                }
            }
            fragmento_estela.rotate(270);
            for (int j = tam_celda/2 - desface; j < tam_celda; j++) {
                for(int i = inicio_x_fragmento + desface; i< inicio_x_fragmento + desface + grosor_fragmento_estela; i++) {
                    fragmento_estela.setColorAt(i, j, color_base_estela);
                }
            }

            if (desface >=0){
                fragmento_estela.rotate(90);
            } else {
                fragmento_estela.rotate(180);
            }
        }

        if (orientacion_actual == Direccion.ESTE) {
            fragmento_estela.rotate(90);
        } else if (orientacion_actual == Direccion.SUR){
            fragmento_estela.rotate(180);
        } else if ( orientacion_actual == Direccion.OESTE){
            fragmento_estela.rotate(270);
        }

        MyGreenfootImage mi_fragmento_estela = new MyGreenfootImage(fragmento_estela); 
        return mi_fragmento_estela;
    }

    private Direccion direccionADerecha(Direccion direccion){
        if(direccion == Direccion.NORTE){
            return Direccion.ESTE;
        } else if (direccion == Direccion.ESTE){
            return Direccion.SUR;
        } else if (direccion == Direccion.SUR) {
            return Direccion.OESTE;
        } else if (direccion == Direccion.OESTE){
            return Direccion.NORTE;
        }
        
        throw new IllegalArgumentException("Dirección no válida: " + direccion);
    }

    /*
     * Establece el aura de la nave.
     * 
     * @param aura El indice del color del aura a aplicar.
     */
    public void establecerAura(int aura) {
        this.aura = aura;
        actualizarImagen();
    }


    @Override
    protected void actualizarImagen() {
        int tamCelda = getWorld().getCellSize();
        GreenfootImage image = getImage();
        image.scale((int) (tamCelda * ESCALA_X), (int) (tamCelda * ESCALA_Y));
        setImage(image);

        MyGreenfootImage canvas = new MyGreenfootImage(imagenBase.getWidth(),
                imagenBase.getHeight() + getWorld().getCellSize() / 3);

        canvas.setColor(Color.BLACK);
        canvas.fillRect(4, imagenBase.getHeight() - 2, getWorld().getCellSize() - 6, 12);
        canvas.setColor(obtenerColorDeBarraIndicadora());

        canvas.fillRect(6, imagenBase.getHeight(),
                (int) ((getWorld().getCellSize() - 10) * obtenerProporcionDeBarraIndicadora()), 8);

        canvas.rotate(360 - direccion.rotacion);

        canvas.drawImage(imagenBase, 0, getWorld().getCellSize() / 6);
        if (aura >= 0){
            Color color_aura = MyGreenfootImage.AURAS[aura % MyGreenfootImage.AURAS.length];
            canvas.highlight(color_aura);
        }
        setImage(canvas);
        
    }
}
