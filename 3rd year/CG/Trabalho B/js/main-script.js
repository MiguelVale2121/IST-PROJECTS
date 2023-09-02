//////////////////////
/* GLOBAL VARIABLES */
//////////////////////
var scene, renderer, currentCamera;
var camera1, camera2, camera3, camera4, camera5;
var materials = [];  
var robotBottom = new THREE.Object3D();
var Rfoot = new THREE.Object3D();
var Lfoot = new THREE.Object3D();
var RotHead = new THREE.Object3D();
var leftArm;
var rightArm;
var keyState = {};
var wireframeEnabled = false;
var state = false;

const key = Array(256).fill(0);
const movementSpeed = 10;
const rotationSpeed = 1;
const armsMovementSpeed = 0.02;
const clock = new THREE.Clock();

/////////////////////
/* CREATE SCENE(S) */
/////////////////////
function createScene(){
    'use strict';
    scene = new THREE.Scene();
    scene.background = new THREE.Color(0xffffff); // Cor clara

    scene.add(new THREE.AxisHelper(10)); // Eixos XYZ

    var robot = createRobot();
    scene.add(robot);

    var trailer = createTrailer();
    scene.add(trailer);
    
}

//////////////////////
/* CREATE CAMERA(S) */
//////////////////////
function createCameras() {
    var aspectRatio = window.innerWidth / window.innerHeight;
    // Câmaras de projecção ortogonal
    camera1 = new THREE.OrthographicCamera(aspectRatio * 15, -aspectRatio * 15, 15, -15, 1, 1000);
    camera1.position.z = 10; // Vista frontal

    camera2 = new THREE.OrthographicCamera(-aspectRatio * 15, aspectRatio * 15, 15, -15, 1, 1000);
    camera2.position.x = 10; // Vista lateral

    camera3 = new THREE.OrthographicCamera(aspectRatio * 10, -aspectRatio * 10, 10, -10, 1, 1000);
    camera3.position.y = 15; // Vista de topo

    // Orthographic isometric view
    camera4 = new THREE.OrthographicCamera(-aspectRatio * 25, aspectRatio * 25, 25, -25, 1, 1000);
    camera4.position.set(25, 25, 25);

    // Perspective isometric view
    camera5 = new THREE.PerspectiveCamera(75, aspectRatio, 0.1, 1000);
    camera5.position.set(15, 15, 15);

    camera1.lookAt(scene.position);
    camera2.lookAt(scene.position);
    camera3.lookAt(scene.position);
    camera4.lookAt(scene.position);
    camera5.lookAt(scene.position);

    currentCamera = camera4;
}

/////////////////////
/* CREATE LIGHT(S) */
/////////////////////

////////////////////////
/* CREATE OBJECT3D(S) */
////////////////////////

function createTrailer() {

    trailer = new THREE.Object3D();

    // Create the container
    var container = createCube(10, 10, 30, 0x808080);  
    container.position.set(0, 6.5, -25); 
    trailer.add(container);

    var wheelPositions = [
        [4.5, -6.5, -8],
        [4.5, -6.5, -13],
        [-4.5, -6.5, -8],
        [-4.5, -6.5, -13],
    ];
    for (var i = 0; i < wheelPositions.length; i++) {
        var wheel = createWheel();
        wheel.position.set(wheelPositions[i][0], wheelPositions[i][1], wheelPositions[i][2]);
        wheel.rotation.x = Math.PI / 2;  // Rotate the wheels to lie flat
        container.add(wheel);
    }

    var connectingPart = createCube(2, 3, 2, 0xFF0000);  
    connectingPart.position.set(0, -3.5, 16);  
    container.add(connectingPart);


    trailer.add(container);

    trailer.userData = {
        min: new THREE.Vector3(5.5,2,-40.5), 
        max: new THREE.Vector3(-5.5,11,-9.5),
    }

    scene.add(trailer);

}

function createCube(x, y, z, color) {
    var geometry = new THREE.BoxGeometry(x, y, z);
    var material = new THREE.MeshBasicMaterial({ color: color });
    materials.push(material); 
    return new THREE.Mesh(geometry, material);
}

function createCylinder(radiusTop, radiusBottom, height, color) {
    var geometry = new THREE.CylinderGeometry(radiusTop, radiusBottom, height, 32);
    var material = new THREE.MeshBasicMaterial({ color: color });
    materials.push(material);  
    return new THREE.Mesh(geometry, material);
}

function createHead() {
    var head = new THREE.Object3D();

    var headCube = createCube(2, 2, 2, 0x00008b);  
    head.add(headCube);

    return head;
}

function createEye() {
    var eye = new THREE.Object3D();

    var eyeCube = createCube(0.3, 0.3, 0.3, 0xffff00);  
    eye.add(eyeCube);

    return eye;
}

function createAntenna() {
    var antenna = new THREE.Object3D();

    var antennaCylinder = createCylinder(0.2, 0.2, 1, 0x00008b);  
    antenna.add(antennaCylinder);

    return antenna;
}

function createForearm() {
    var forearm = new THREE.Object3D();

    var forearmCube = createCube(3, 1.5, 6, 0x8b0000);  
    forearm.add(forearmCube);

    return forearm;
}

function createArm() {
    var arm = new THREE.Object3D();

    var armCube = createCube(3, 5, 2, 0x8b0000); 
    arm.add(armCube);

    return arm;
}

function createPipe() {
    var pipe = new THREE.Object3D();

    var pipeCylinder = createCylinder(0.5, 0.5, 5, 0xc0c0c0); 
    pipe.add(pipeCylinder);

    return pipe;
}

function createTorso() {
    var torso = new THREE.Object3D();

    var torsoCube = createCube(8, 5, 4, 0xff0000);  
    torso.add(torsoCube);

    return torso;
}

function createAbdomen() {
    var abdomen = new THREE.Object3D();

    var abdomenCube = createCube(2, 2, 4, 0xc0c0c0);  
    abdomen.add(abdomenCube);

    return abdomen;
}

function createWaist() {
    var waist = new THREE.Object3D();

    var waistCube = createCube(6, 2, 4, 0xc0c0c0);
    waist.add(waistCube);


    return waist;
}

function createThigh() {
    var thigh = new THREE.Object3D();

    var thighCube = createCube(1, 4, 1, 0xc0c0c0); 
    thigh.add(thighCube);

    return thigh;
}

function createLeg() {
    var leg = new THREE.Object3D();

    var legCube = createCube(3, 8, 3, 0x00008b);  
    leg.add(legCube);

    return leg;
}

function createFoot() {
    var foot = new THREE.Object3D();

    var footCube = createCube(4, 2, 3, 0x00008b);  
    foot.add(footCube);

    return foot;
}

function createWheel(){
    var wheel = new THREE.Object3D();

    var wheelCylinder = createCylinder(1.5, 1.5, 1, 0x000000);  
    wheelCylinder.rotation.z = Math.PI / 2;  // Rotate the wheels to lie flat
    wheel.add(wheelCylinder);

    return wheel;
}

function createRobot() {
    robot = new THREE.Object3D();

    robot.userData = {
        min: new THREE.Vector3(-3.5,2,1.5), 
        max: new THREE.Vector3(3.5,7.5,-3.5),
    }

    var waist = createWaist();
    waist.position.x = 0;
    waist.position.y = 0;
    waist.position.z = 0;
    robot.add(waist);

    var rightWaistWheel = createWheel();
    rightWaistWheel.position.x = 3.5;
    waist.add(rightWaistWheel);

    var leftWaistWheel = createWheel();
    leftWaistWheel.position.x = -3.5;
    waist.add(leftWaistWheel);

    //################### BOTTOM ##################################
    
    robotBottom.position.set(0, 0, 0);
    waist.add(robotBottom);
        
    var leftThigh = createThigh();
    leftThigh.position.x = -1.5;
    leftThigh.position.y = -3;
    robotBottom.add(leftThigh);

    var rightThigh = createThigh();
    rightThigh.position.x = 1.5;
    rightThigh.position.y = -3;
    robotBottom.add(rightThigh);

    var leftLeg = createLeg();
    leftLeg.position.y = -6;
    leftThigh.add(leftLeg);

    var leftTopWheel = createWheel();
    leftTopWheel.position.x = -2;
    leftTopWheel.position.y = 1.5;
    leftLeg.add(leftTopWheel);

    var leftBottomWheel = createWheel();
    leftBottomWheel.position.x = -2;
    leftBottomWheel.position.y = -2.5;
    leftLeg.add(leftBottomWheel);

    var rightLeg = createLeg();
    rightLeg.position.y = -6;
    rightThigh.add(rightLeg);

    var rightTopWheel = createWheel();
    rightTopWheel.position.x = 2;
    rightTopWheel.position.y = 1.5;
    rightLeg.add(rightTopWheel);

    var rightBottomWheel = createWheel();
    rightBottomWheel.position.x = 2;
    rightBottomWheel.position.y = -2.5;
    rightLeg.add(rightBottomWheel);

    Rfoot.position.set(0,-2.5,0);
    rightLeg.add(Rfoot);

    Lfoot.position.set(0,-2.5,0);
    leftLeg.add(Lfoot)

    var leftFoot = createFoot();
    leftFoot.position.y = -0.5;
    leftFoot.position.x = -0.5;
    leftFoot.position.z = 3;
    Lfoot.add(leftFoot);

    var rightFoot = createFoot();
    rightFoot.position.y = -0.5;
    rightFoot.position.x = 0.5;
    rightFoot.position.z = 3;
    Rfoot.add(rightFoot);

    //################### TOP ##################################

    var abdomen = createAbdomen();
    abdomen.position.y = 2;
    waist.add(abdomen);

    var torso = createTorso();
    torso.position.y = 3.5;
    abdomen.add(torso);

    leftArm = createArm();
    leftArm.position.x = -5.5;
    leftArm.position.z = -3;
    torso.add(leftArm);

    rightArm = createArm();
    rightArm.position.x = 5.5;
    rightArm.position.z = -3;
    torso.add(rightArm);

    var leftForearm = createForearm();
    leftForearm.position.y = -3.25;
    leftForearm.position.z = 2;
    leftArm.add(leftForearm);

    var rightForearm = createForearm();
    rightForearm.position.y = -3.25;
    rightForearm.position.z = 2;
    rightArm.add(rightForearm);

    var leftPipe = createPipe();
    leftPipe.position.x = -2;
    leftPipe.position.y = 1;
    leftArm.add(leftPipe);

    var rightPipe = createPipe();
    rightPipe.position.x = 2;
    rightPipe.position.y = 1;
    rightArm.add(rightPipe);

    RotHead.position.set(0,2.5,0);
    torso.add(RotHead);

    var head = createHead();
    head.position.y = 1.01;
    RotHead.add(head);

    var leftEye = createEye();
    leftEye.position.x = -0.5;
    leftEye.position.y = 0.3;
    leftEye.position.z = 1;
    head.add(leftEye);

    var rightEye = createEye();
    rightEye.position.x = 0.5;
    rightEye.position.y = 0.3;
    rightEye.position.z = 1;
    head.add(rightEye);

    var leftAntenna = createAntenna();
    leftAntenna.position.x = -0.8;
    leftAntenna.position.y = 1.5;
    head.add(leftAntenna);

    var rightAntenna = createAntenna();
    rightAntenna.position.x = 0.8;
    rightAntenna.position.y = 1.5;
    head.add(rightAntenna);

    scene.add(robot);

}
//////////////////////
/* CHECK COLLISIONS */
//////////////////////
function checkCollisions() {
    'use strict';

    if((robot.userData.max.z < trailer.userData.max.z &&  trailer.userData.min.z < robot.userData.min.z) 
        && (trailer.userData.min.x > robot.userData.min.x && trailer.userData.max.x < robot.userData.max.x)) {
        
        return true;
    }

    return false;
}

///////////////////////
/* HANDLE COLLISIONS */
///////////////////////
function handleCollisions(){
    'use strict';
    if(checkCollisions() && state == true){
        console.log("COLLISION");
        return true;
    }
    return false;
}

////////////
/* UPDATE */
////////////
function update() {
    'use strict';

    let delta = clock.getDelta();

    let xDiff = key[39] - key[37];
    let zDiff = key[38] - key[40];

    let motion = new THREE.Vector3(xDiff, 0, zDiff).normalize().multiplyScalar(movementSpeed * delta);
    let inverseMotion = new THREE.Vector3(-xDiff, 0, -zDiff).normalize().multiplyScalar(movementSpeed * delta);


    trailer.position.add(motion);
    trailer.userData.max.add(motion);
    trailer.userData.min.add(motion);

    robotBottom.rotation.x = THREE.MathUtils.clamp(robotBottom.rotation.x + (key[87] - key[83]) * rotationSpeed * delta, 0, Math.PI/2);

    Rfoot.rotation.x = THREE.MathUtils.clamp(Rfoot.rotation.x + (key[81] - key[65]) * rotationSpeed * delta, 0, Math.PI / 2);
    Lfoot.rotation.x = THREE.MathUtils.clamp(Lfoot.rotation.x + (key[81] - key[65]) * rotationSpeed * delta, 0, Math.PI / 2);

    RotHead.rotation.x = THREE.MathUtils.clamp(RotHead.rotation.x + (key[82] - key[70]) * rotationSpeed * delta, 0, Math.PI);
    
    if (robotBottom.rotation.x == Math.PI/2 && Rfoot.rotation.x == Math.PI/2 && Lfoot.rotation.x == Math.PI/2 && RotHead.rotation.x == Math.PI && leftArm.position.x == -2.5 && rightArm.position.x == 2.5) {
        state = true;
    }
    else {
        state = false;
    }


    if (key[54] && !keyState[54]) {
        wireframeEnabled = !wireframeEnabled;
        for (var i = 0; i < materials.length; i++) {
            materials[i].wireframe = wireframeEnabled;
        }
    }
    keyState[54] = key[54];

    if (key[68] || key[100]){
        leftArm.position.x -= armsMovementSpeed;
        rightArm.position.x += armsMovementSpeed;

        if (leftArm.position.x < -5.5 && rightArm.position.x  > 5.5 ) {
            leftArm.position.x = -5.5
            rightArm.position.x = 5.5
        }
    }
    if (key[69] || key[101]){
        leftArm.position.x += armsMovementSpeed;
        rightArm.position.x -= armsMovementSpeed;
        if (leftArm.position.x > -2.5 && rightArm.position.x < 2.5) {
            leftArm.position.x = -2.5
            rightArm.position.x = 2.5
        }
    }
    if (key[49]) currentCamera = camera1;
    if (key[50]) currentCamera = camera2;
    if (key[51]) currentCamera = camera3;
    if (key[52]) currentCamera = camera4;
    if (key[53]) currentCamera = camera5;
    

    if(handleCollisions()) {
        trailer.position.set(0,0,6);
    };
}


/////////////
/* DISPLAY */
/////////////
function render() {
    'use strict';
    renderer.render(scene, currentCamera);

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

    createScene();

    createCameras();

    render();

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
    camera1.aspect = window.innerWidth / window.innerHeight;
    camera2.aspect = window.innerWidth / window.innerHeight;
    camera3.aspect = window.innerWidth / window.innerHeight;
    camera4.aspect = window.innerWidth / window.innerHeight;
    camera5.aspect = window.innerWidth / window.innerHeight;

    camera1.updateProjectionMatrix();
    camera2.updateProjectionMatrix();
    camera3.updateProjectionMatrix();
    camera4.updateProjectionMatrix();
    camera5.updateProjectionMatrix();

}

///////////////////////
/* KEY DOWN CALLBACK */
///////////////////////
function onKeyDown(e) {
    'use strict';
    key[e.keyCode] = 1;

}

///////////////////////
/* KEY UP CALLBACK */
///////////////////////
function onKeyUp(e){
    'use strict';
    key[e.keyCode] = 0;
        
}