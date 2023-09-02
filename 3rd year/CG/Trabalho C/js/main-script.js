//////////////////////
/* GLOBAL VARIABLES */
//////////////////////
let scene, renderer, camera, controls;
let mesh;
let loader = new THREE.TextureLoader();
let terrain, dome, moon, ufoBody, cockpit, cylinder;
let directionalLight, moonLightOn = true;
let pointLight, pointLightOn = true ;
let spotLight, spotLightOn = true
let ufoGroup = new THREE.Group();
const movementSpeed = 10;
const rotationSpeed = 1;
const clock = new THREE.Clock();
const key = Array(256).fill(0);
const pointLights = [];
let ufoSpheres = [];
let leaves1 = [];
let trunks = [];
let leaves2 = [];
let branches = [];
let houseObjects = [];


const shadingTypes = [
    THREE.FlatShading, // Gouraud
    THREE.SmoothShading, // Phong
    THREE.SmoothShading // Cartoon (usando o mesmo tipo de shading do Phong)
  ];

/////////////////////
/* CREATE SCENE(S) */
/////////////////////

function createScene() {
    'use strict';

    scene = new THREE.Scene();
    

    
}

//////////////////////
/* CREATE CAMERA(S) */
//////////////////////
function createCamera(){
    'use strict';

    camera = new THREE.PerspectiveCamera(30, window.innerWidth / window.innerHeight, 0.3, 3000);
    camera.position.set(70, 70, 70); 
    camera.lookAt(0, 0, 0); 
    
    
}

/////////////////////
/* CREATE LIGHT(S) */
/////////////////////
function createAmbLight() {
    'use strict';

  // Add lights to the scene
  const ambientLight = new THREE.AmbientLight(0xffffff, 0.3);
  scene.add(ambientLight);


}

////////////////////////
/* CREATE OBJECT3D(S) */
////////////////////////


function createTerrain() {
    'use strict';

    let geometry = new THREE.PlaneGeometry(100, 100, 64,64);
    let material = new THREE.MeshPhongMaterial({    
        displacementMap: loader.load('./js/elevation.png'),
        displacementScale: 80,
        displacementBias: -5,
    });

    terrain = new THREE.Mesh(geometry, material);
    scene.add(terrain);
    terrain.rotation.x = -Math.PI / 2;
    terrain.position.y = -4.5; 
}

function createDome() {
    let radius = 50.5; 
    let segmentsX = 32;
    let segmentsY = 32;

    let domeGeometry = new THREE.SphereGeometry(radius, segmentsX, segmentsY, 0, Math.PI * 2, 0, Math.PI/2 );
    let domeMaterial = new THREE.MeshPhongMaterial({
        side: THREE.BackSide, 
    });
    dome = new THREE.Mesh(domeGeometry, domeMaterial);
    scene.add(dome);
    dome.position.set(0, -4.5, 0); 

}


function createMoon() {
    let radius = 2.5;
    let segmentsX = 10;
    let segmentsY = 32;

    let moonGeometry = new THREE.SphereGeometry(radius, segmentsX, segmentsY, 0);
    let moonMaterial = new THREE.MeshStandardMaterial({
        color: 0xffd45f ,
        emissive: 0xffd45f ,
        emissiveIntensity: 0.5,
    });
    moon = new THREE.Mesh(moonGeometry, moonMaterial);
    scene.add(moon);
    moon.position.set(7, 24, 0);




    directionalLight = new THREE.DirectionalLight(0xffd45f, 1);
    directionalLight.position.set(7, 24, 0); 
    directionalLight.target.position.set(-1, -1, 0); 
    directionalLight.target.updateMatrixWorld();

    scene.add(directionalLight);



}

function createUFO() {
    'use strict';
    var height = 8;
    const bodyMaterial = new THREE.MeshLambertMaterial({ color: 0x888888 });
    const bodyGeometry = new THREE.SphereGeometry(2, 32, 32);
    bodyGeometry.scale(1, 0.3, 1);
    ufoBody = new THREE.Mesh(bodyGeometry, bodyMaterial);
    ufoBody.position.set(0, 0, 0);
    ufoGroup.add(ufoBody);
  
    const cockpitMaterial = new THREE.MeshLambertMaterial({ color: 0x222222 });
    const cockpitGeometry = new THREE.SphereGeometry(0.8, 32, 32, 0, Math.PI * 2, 0, Math.PI / 2); 
    cockpit = new THREE.Mesh(cockpitGeometry, cockpitMaterial);
    cockpit;
    cockpit.position.set(0,0.5,0);
    ufoGroup.add(cockpit);


    const cylinderMaterial = new THREE.MeshLambertMaterial({ color: 0x666666 });
    const cylinderGeometry = new THREE.CylinderGeometry(0.8, 0.8, 0.7, 32);
    cylinder = new THREE.Mesh(cylinderGeometry, cylinderMaterial);


    const target = new THREE.Object3D();
    target.position.set(0, 0 ,0);
    cylinder.add(target);
    spotLight = new THREE.SpotLight(0xffffff, 1 );
    spotLight.target = target;
    spotLight.angle = Math.PI / 8;
    cylinder.add(spotLight);


    cylinder.position.set(0, -0.5, 0);
    ufoGroup.add(cylinder);


    const sphereMaterial = new THREE.MeshLambertMaterial({ color: 0x444444 });
    const sphereGeometry = new THREE.SphereGeometry(0.25, 16, 16);
    for (let i = 0; i < 8; i++) {
        const sphere = new THREE.Mesh(sphereGeometry, sphereMaterial);
        ufoSpheres.push(sphere);

        sphere.position.set(1.5 * Math.cos((i * Math.PI) / 4) , -0.4 , 1.5 * Math.sin((i * Math.PI) / 4));
        pointLight = new THREE.PointLight(0x4444ff, 0.1, 20);
        pointLight.position.copy(sphere.position);
        ufoGroup.add(pointLight);
        ufoGroup.add(sphere);
        pointLights.push(pointLight);

    } 

    
    scene.add(ufoGroup); 
    ufoGroup.position.set(-5,9.5 + height,0);


}

////////////////////////////
/* TEXTURE GENERATION */
////////////////////////////
function createSobreiro(size, rotation, px,py,pz) {
    const trunkMaterial = new THREE.MeshPhongMaterial({ color: 0x8B4513 });
    const leafMaterial = new THREE.MeshPhongMaterial({ color: 0x00FF00 });
  
    const trunkGeometry = new THREE.CylinderGeometry(0.4, 0.4, 5, 32);
    const trunk = new THREE.Mesh(trunkGeometry, trunkMaterial);
    trunk.rotation.x = Math.PI / 9;
  
    const branchGeometry = new THREE.CylinderGeometry(0.2, 0.2, 2, 32);
    const branch = new THREE.Mesh(branchGeometry, trunkMaterial);
    branch.position.y = 0.4;
    branch.position.z = -1;
    branch.rotation.x = -Math.PI / 4;

  
  const leafGeometry1 = new THREE.SphereGeometry(0.7, 32);
  const leafGeometry2 = new THREE.SphereGeometry(0.4, 32);

  const leaf1 = new THREE.Mesh(leafGeometry1, leafMaterial);
  leaf1.position.y = 3;
  leaf1.scale.x = 3; 
  leaf1.scale.y = 1.3; 


  const leaf2 = new THREE.Mesh(leafGeometry2, leafMaterial);
  leaf2.position.y = 1.3;
  leaf2.scale.x = 3; 
  leaf2.scale.y = 1.3; 
  
    trunk.add(branch);
    trunk.add(leaf1);
    branch.add(leaf2);
    scene.add(trunk);

    trunk.rotation.y = rotation;
    trunk.scale.x = size;
    trunk.scale.y = size;
    trunk.scale.z = size;

    trunk.position.set(px,py,pz);

    leaves2.push(leaf2);
    leaves1.push(leaf1);
    trunks.push(trunk);
    branches.push(branch);
  }

function spreadSobreiros() {
    createSobreiro(1, -Math.PI/10, -5,0,-20);
    createSobreiro(0.9,Math.PI/9, -10,0,5);
    createSobreiro(1.7, Math.PI/6, -10,0,5);
    createSobreiro(2.1, -Math.PI/5, 12,0,-10);
    createSobreiro(1.4, Math.PI/4, -15,0,-10);
    createSobreiro(2, -Math.PI/3, 10,0,10);


}


function createHouse() {
    'use strict';

    let house = new THREE.Group();

    const wall1 = new THREE.BufferGeometry()

    const vertices = new Float32Array( [
        -12, 0, 0, // 0
        -12, 10, 0, // 1
        -10, 2, 0, // 2
        -3, 0, 0, // 3
        -7, 2, 0, // 4
        -3, 2, 0, // 5
        -10, 8, 0, // 6
        -10, 10, 0, // 7
        -7, 8, 0, // 8
        -3, 10, 0, // 9
        -3, 7, 0, // 10
        0, 7, 0, // 11
        3, 10, 0, // 12
        3, 7, 0, // 13
        7, 8, 0, // 14
        3, 2, 0, // 15
        7, 2, 0, // 16
        3, 0, 0, // 17
        10, 2, 0, // 18
        12, 0, 0, // 19
        12, 10, 0, // 20
        10, 8, 0, // 21
        10, 10, 0, // 22

    ] );  

    const indices = [
        2, 1, 0,
        3, 2, 0,
        2, 3, 4,
        2, 4, 5,
        5, 4, 3,
        4, 5, 8,
        1, 2, 6,
        1, 6, 7,
        8, 7, 6,
        7, 8, 9,
        10, 9, 8,
        5, 10, 8,
        9, 10, 11,
        9, 11, 12,
        13, 12, 11,
        12, 13, 14,
        15, 14, 13,
        14, 15, 16,
        17, 16, 15,
        16, 17, 18,
        19, 18, 17,
        18, 19, 20,
        18, 20, 21,
        22, 21, 20,
        14, 21, 22,
        12, 14, 22,
    ];
    
    wall1.setIndex( indices );
    wall1.setAttribute( 'position', new THREE.BufferAttribute( vertices, 3 ) );

    wall1.computeVertexNormals();

    const material = new THREE.MeshStandardMaterial( { color: 0xffffff } );
    const frontwall = new THREE.Mesh( wall1, material );

    house.add(frontwall);

    const wall2 = new THREE.BufferGeometry()

    const vertices2 = new Float32Array( [
        -12, 0, 0, // 0
        -12, 10, 0, // 1
         12, 0, 0, // 2
         12, 10, 0, // 3
    ] );

    const indices2 = [
        2, 1, 0,
        3, 1, 2,
    ];  


    wall2.setIndex( indices2 );
    wall2.setAttribute( 'position', new THREE.BufferAttribute( vertices2, 3 ) );
    wall2.computeVertexNormals();

    const rightsidewall = new THREE.Mesh( wall2, material );
    rightsidewall.position.set(12,0,-12);
    rightsidewall.rotation.y = Math.PI/2;

    house.add(rightsidewall);

    const leftsidewall = new THREE.Mesh( wall2, material );
    leftsidewall.position.set(-12,0,-12);
    leftsidewall.rotation.y = 3*Math.PI/2;

    house.add(leftsidewall);

    const backwall = new THREE.Mesh( wall2, material );
    backwall.position.set(0,0,-24);
    backwall.rotation.y = Math.PI;

    house.add(backwall);

    const wall3 = new THREE.BufferGeometry()

    const vertices3 = new Float32Array( [
        -12, 0, 0, // 0
        -12, 8 * Math.sqrt(3), 0, // 1
         12, 0, 0, // 2
         12, 8 * Math.sqrt(3), 0, // 3
    ] );

    const indices3 = [
        2, 1, 0,
        3, 1, 2,
    ];  

    wall3.setIndex( indices3 );
    wall3.setAttribute( 'position', new THREE.BufferAttribute( vertices3, 3 ) );
    wall3.computeVertexNormals();

    const material1 = new THREE.MeshStandardMaterial( { color: 0xffa500 } );
    const frontroof = new THREE.Mesh( wall3, material1 );

    frontroof.position.set(0,10,0);
    frontroof.rotation.x = -Math.PI/3;

    house.add(frontroof);

    const backroof = new THREE.Mesh( wall3, material1 );

    backroof.position.set(0,10,-24);
    backroof.rotation.x = Math.PI/3;
    backroof.rotation.y = Math.PI;

    house.add(backroof);

    const wall4 = new THREE.BufferGeometry()

    const vertices4 = new Float32Array( [
        -12, 0, 0, // 0
         0, 4 * Math.sqrt(3), 0, // 1
         12, 0, 0, // 2
    ] );

    const indices4 = [
        2, 1, 0,
    ];  

    wall4.setIndex( indices4 );
    wall4.setAttribute( 'position', new THREE.BufferAttribute( vertices4, 3 ) );
    wall4.computeVertexNormals();

    const leftroof = new THREE.Mesh( wall4, material1 );
    leftroof.position.set(-12,10,-12);
    leftroof.rotation.y = 3*Math.PI/2;

    house.add(leftroof);

    const rightroof = new THREE.Mesh( wall4, material1 );
    rightroof.position.set(12,10,-12);
    rightroof.rotation.y = Math.PI/2;

    house.add(rightroof);

    const window = new THREE.BufferGeometry()

    const vertices5 = new Float32Array( [
        -10, 2, 0, // 0
        -10, 8, 0, // 1
        -7, 2, 0, // 2
        -7, 8, 0, // 3
    ] );

    const indices5 = [
        2, 1, 0,
        3, 1, 2,
    ];

    window.setIndex( indices5 );
    window.setAttribute( 'position', new THREE.BufferAttribute( vertices5, 3 ) );
    window.computeVertexNormals();

    const material2 = new THREE.MeshStandardMaterial( { color: 0x0000ff } );
    const leftwindow = new THREE.Mesh( window, material2 );

    house.add(leftwindow);

    const rightwindow = new THREE.Mesh( window, material2 );
    rightwindow.position.set(17,0,0);

    house.add(rightwindow);

    const door = new THREE.BufferGeometry();

    const vertices6 = new Float32Array( [
        -3, 0, 0, // 0
        -3, 7, 0, // 1
        3, 0, 0, // 2
        3, 7, 0, // 3
    ] );

    const indices6 = [
        2, 1, 0,
        3, 1, 2,
    ];

    door.setIndex( indices6 );
    door.setAttribute( 'position', new THREE.BufferAttribute( vertices6, 3 ) );
    door.computeVertexNormals();

    const frontDoor = new THREE.Mesh( door, material2 );

    house.add(frontDoor);


    scene.add(house);

    house.position.set(-2,-2,0);

    house.scale.set(0.5,0.5,0.5);

    houseObjects.push(frontwall);
    houseObjects.push(backwall);
    houseObjects.push(rightsidewall);
    houseObjects.push(leftsidewall);
    houseObjects.push(backroof);
    houseObjects.push(frontroof);
    houseObjects.push(rightroof);
    houseObjects.push(leftroof);
    houseObjects.push(frontDoor);
    houseObjects.push(rightwindow);
    houseObjects.push(leftwindow);

}

function createMoonMaterials() {
    moonMaterials = [
        new THREE.MeshLambertMaterial({ color: '#ffd45f',}),
        new THREE.MeshPhongMaterial({ color: '#ffd45f',}),
        new THREE.MeshToonMaterial({color: '#ffd45f', }),
        new THREE.MeshBasicMaterial({ color: '#ffd45f', })
    ];
}

function createUfoMaterials() {
    ufoMaterials = {
        bodyMaterial: [
            new THREE.MeshLambertMaterial({ color: 0x888888 }),
            new THREE.MeshPhongMaterial({ color: 0x888888 }),
            new THREE.MeshToonMaterial({ color: 0x888888 }),
            new THREE.MeshBasicMaterial({ color: 0x888888 })
        ],
        cockpitMaterial: [
            new THREE.MeshLambertMaterial({ color: 0x222222 }),
            new THREE.MeshPhongMaterial({ color: 0x222222 }),
            new THREE.MeshToonMaterial({ color: 0x222222 }),
            new THREE.MeshBasicMaterial({ color: 0x222222 })
        ],
        cylinderMaterial: [
            new THREE.MeshLambertMaterial({ color: 0x666666 }),
            new THREE.MeshPhongMaterial({ color: 0x666666 }),
            new THREE.MeshToonMaterial({ color: 0x666666 }),
            new THREE.MeshBasicMaterial({ color: 0x666666 })
        ],
        sphereMaterial: [
            new THREE.MeshLambertMaterial({ color: 0x444444 }),
            new THREE.MeshPhongMaterial({ color: 0x444444 }),
            new THREE.MeshToonMaterial({ color: 0x444444 }),
            new THREE.MeshBasicMaterial({ color: 0x444444 })
        ]
    };
}

function createSobreiroMaterials() {
    sobreiroMaterials = {
        trunkMaterial: [
            new THREE.MeshLambertMaterial({ color: 0x8b4513 }),
            new THREE.MeshPhongMaterial({ color: 0x8b4513 }),
            new THREE.MeshToonMaterial({ color: 0x8b4513 }),
            new THREE.MeshBasicMaterial({ color: 0x8b4513 })
        ],
        leavesMaterial: [
            new THREE.MeshLambertMaterial({ color: 0x006400 }),
            new THREE.MeshPhongMaterial({ color: 0x006400 }),
            new THREE.MeshToonMaterial({ color: 0x006400 }),
            new THREE.MeshBasicMaterial({ color: 0x006400 })
        ]
    };
}

function createHouseMaterials() {
    houseMaterials = {
        walls: [
            new THREE.MeshLambertMaterial({ color: 0xffffff }),
            new THREE.MeshPhongMaterial({ color: 0xffffff }),
            new THREE.MeshToonMaterial({ color: 0xffffff }),
            new THREE.MeshBasicMaterial({ color: 0xffffff })
        ],
        roof: [
            new THREE.MeshLambertMaterial({ color: 0xffa500 }),
            new THREE.MeshPhongMaterial({ color: 0xffa500 }),
            new THREE.MeshToonMaterial({ color: 0xffa500 }),
            new THREE.MeshBasicMaterial({ color: 0xffa500 })
        ],
        windows: [
            new THREE.MeshLambertMaterial({ color: 0x0000ff }),
            new THREE.MeshPhongMaterial({ color: 0x0000ff }),
            new THREE.MeshToonMaterial({ color: 0x0000ff }),
            new THREE.MeshBasicMaterial({ color: 0x0000ff })
        ]    
    }
}


function createTexture(choice) {
    'use strict';

    let canvas = document.createElement('canvas');
    canvas.width = 256;
    canvas.height = 256;
    let context = canvas.getContext('2d');

    if (choice === 1) {
        // floral field texture
        context.fillStyle = '#90ee90'; // light green
        context.fillRect(0, 0, canvas.width, canvas.height);
        let colors = ['#ffffff', '#ffff00', '#dda0dd', '#add8e6']; // white, yellow, lilac, light blue
        for (let i = 0; i < 50; i++) {
            let x = Math.random() * canvas.width;
            let y = Math.random() * canvas.height;
            let r = Math.random() * 2;
            context.fillStyle = colors[Math.floor(Math.random() * colors.length)];
            context.beginPath();
            context.arc(x, y, r, 0, 2 * Math.PI);
            context.fill();
        }
    } else if (choice === 2) {
        // starry sky texture
        let gradient = context.createLinearGradient(0, 0, 0, canvas.height);
        gradient.addColorStop(0, '#00008b'); // dark blue
        gradient.addColorStop(1, '#800080'); // dark violet
        context.fillStyle = gradient;
        context.fillRect(0, 0, canvas.width, canvas.height);
        context.fillStyle = '#ffffff'; // white
        for (let i = 0; i < 50; i++) {
            let x = Math.random() * canvas.width;
            let y = Math.random() * canvas.height;
            let r = Math.random() * 2;
            context.beginPath();
            context.arc(x, y, r, 0, 2 * Math.PI);
            context.fill();
        }
    }

    let texture = new THREE.Texture(canvas);
    texture.wrapS = THREE.RepeatWrapping;
    texture.wrapT = THREE.RepeatWrapping;
    texture.needsUpdate = true;
    return texture;
}



////////////
/* UPDATE */
////////////

function update(){
    'use strict';

    let delta = clock.getDelta();
    

    let xDiff = key[39] - key[37];


    let motion = new THREE.Vector3(xDiff, 0, 0).normalize().multiplyScalar(movementSpeed * delta);

    ufoGroup.position.add(motion);
    
    ufoGroup.rotation.y += (key[40] - key[38]) * rotationSpeed * delta;



}

/////////////
/* DISPLAY */
/////////////
function render() {
    'use strict';
    renderer.render(scene, camera);
    renderer.xr.enabled = true;
    renderer.setAnimationLoop( function () {

        renderer.render( scene, camera );
    
    } );
}

////////////////////////////////
/* INITIALIZE ANIMATION CYCLE */
////////////////////////////////
function init() {
    'use strict';

    renderer = new THREE.WebGLRenderer({
        antialias: true
    });
    renderer.setSize(window.innerWidth, window.innerHeight);
    document.body.appendChild(renderer.domElement);
    let vr = VRButton.createButton( renderer );
    document.body.appendChild( vr );

    createScene();
    createCamera();
    createAmbLight();
    createTerrain();
    createDome();
    createMoon();
    createHouse();
    createUFO();
    spreadSobreiros(); 
    createHouseMaterials();
    createMoonMaterials();
    createUfoMaterials();
    createSobreiroMaterials();


    window.addEventListener('keydown', onKeyDown);
    window.addEventListener('keyup', onKeyUp);
    window.addEventListener('resize', onResize);

    animate();
}

/////////////////////
/* ANIMATION CYCLE */
/////////////////////
function animate() {
    'use strict';

    update();
    render();
    requestAnimationFrame(animate);
    
    
    
}

////////////////////////////
/* RESIZE WINDOW CALLBACK */
////////////////////////////
function onResize() {
    'use strict';

    renderer.setSize(window.innerWidth, window.innerHeight);
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
}

///////////////////////
/* KEY DOWN CALLBACK */
///////////////////////
function onKeyDown(e) {
    'use strict';
    key[e.keyCode] = 1;

    if (e.key === '1') {
        terrain.material.map = createTexture(1);
        terrain.material.needsUpdate = true;
    } else if (e.key === '2') {
        dome.material.map = createTexture(2);
        dome.material.needsUpdate = true;
    }
    else if (e.key.toLowerCase() === 'd') { 
        moonLightOn = !moonLightOn;
        directionalLight.visible = moonLightOn;
    }
    else if (e.key.toLowerCase() === 'p') {
        pointLightOn = !pointLightOn;
        for (let i = 0; i < pointLights.length; i++) {
            pointLight = pointLights[i];
            pointLight.visible = pointLightOn;
        }
        
        
    }
    else if (e.key.toLowerCase() === 's') { 
        spotLightOn = !spotLightOn;
        spotLight.visible = spotLightOn;
    }

    else if (e.key.toLowerCase() == 'q') {
        moon.material = moonMaterials[0];
        ufoBody.material = ufoMaterials.bodyMaterial[0];
        cockpit.material = ufoMaterials.cockpitMaterial[0];
        cylinder.material = ufoMaterials.cylinderMaterial[0]; 

        houseObjects[0].material = houseObjects[1].material = houseObjects[2].material = houseObjects[3].material = houseMaterials.walls[0];
        houseObjects[4].material = houseObjects[5].material = houseObjects[6].material = houseObjects[7].material = houseMaterials.roof[0];
        houseObjects[8].material = houseObjects[9].material = houseObjects[10].material = houseMaterials.windows[0];
        
        
        for (let i = 0; i < ufoSpheres.length; i++) {
            ufoSpheres[i].material = ufoMaterials.sphereMaterial[0];
        }

        for (let i = 0; i < trunks.length; i++) {
            trunks[i].material = branches[i].material = sobreiroMaterials.trunkMaterial[0];
            leaves1[i].material = leaves2[i].material = sobreiroMaterials.leavesMaterial[0];

            
        }
    }
    else if (e.key.toLowerCase() == 'w') {

        moon.material = moonMaterials[1];
        ufoBody.material = ufoMaterials.bodyMaterial[1];
        cockpit.material = ufoMaterials.cockpitMaterial[1];
        cylinder.material = ufoMaterials.cylinderMaterial[1]; 

        houseObjects[0].material = houseObjects[1].material = houseObjects[2].material = houseObjects[3].material = houseMaterials.walls[1];
        houseObjects[4].material = houseObjects[5].material = houseObjects[6].material = houseObjects[7].material = houseMaterials.roof[1];
        houseObjects[8].material = houseObjects[9].material = houseObjects[10].material = houseMaterials.windows[1];



        for (let i = 0; i < ufoSpheres.length; i++) {
            ufoSpheres[i].material = ufoMaterials.sphereMaterial[1];
        }

        for (let i = 0; i < trunks.length; i++) {
            trunks[i].material = branches[i].material = sobreiroMaterials.trunkMaterial[1];
            leaves1[i].material = leaves2[i].material = sobreiroMaterials.leavesMaterial[1];
        }
    }
    else if (e.key.toLowerCase() == 'e') {
        moon.material = moonMaterials[2];
        ufoBody.material = ufoMaterials.bodyMaterial[2];
        cockpit.material = ufoMaterials.cockpitMaterial[2];
        cylinder.material = ufoMaterials.cylinderMaterial[2]; 


        houseObjects[0].material = houseObjects[1].material = houseObjects[2].material = houseObjects[3].material = houseMaterials.walls[2];
        houseObjects[4].material = houseObjects[5].material = houseObjects[6].material = houseObjects[7].material = houseMaterials.roof[2];
        houseObjects[8].material = houseObjects[9].material = houseObjects[10].material = houseMaterials.windows[2];



        for (let i = 0; i < ufoSpheres.length; i++) {
            ufoSpheres[i].material = ufoMaterials.sphereMaterial[2];
        }

        for (let i = 0; i < trunks.length; i++) {
            trunks[i].material = branches[i].material = sobreiroMaterials.trunkMaterial[2];
            leaves1[i].material = leaves2[i].material = sobreiroMaterials.leavesMaterial[2];
        }
    }
    else if (e.key.toLowerCase() == 'r') {
        moon.material = moonMaterials[3];
        ufoBody.material = ufoMaterials.bodyMaterial[3];
        cockpit.material = ufoMaterials.cockpitMaterial[3];
        cylinder.material = ufoMaterials.cylinderMaterial[3]; 


        houseObjects[0].material = houseObjects[1].material = houseObjects[2].material = houseObjects[3].material = houseMaterials.walls[3];
        houseObjects[4].material = houseObjects[5].material = houseObjects[6].material = houseObjects[7].material = houseMaterials.roof[3];
        houseObjects[8].material = houseObjects[9].material = houseObjects[10].material = houseMaterials.windows[3];



        for (let i = 0; i < ufoSpheres.length; i++) {
            ufoSpheres[i].material = ufoMaterials.sphereMaterial[3];
        }

        for (let i = 0; i < trunks.length; i++) {
            trunks[i].material = branches[i].material = sobreiroMaterials.trunkMaterial[3];
            leaves1[i].material = leaves2[i].material = sobreiroMaterials.leavesMaterial[3];
        }
    }
}


///////////////////////
/* KEY UP CALLBACK */
///////////////////////
function onKeyUp(e){
    'use strict';
    key[e.keyCode] = 0;

}