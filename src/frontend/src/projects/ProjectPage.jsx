import React, {useEffect, useRef, useState} from 'react';
import userModule from '../../user.js';
import "../App.css";
import {useNavigate, useParams} from "react-router-dom";
import axios from "axios";


export function ProjectPage({ onLogout }) {
    const [user, setUser] = useState(null);
    const navigate = useNavigate();
    const { projectId } = useParams();
    const [project, setProject] = useState(null);



    const [currentAction, setCurrentAction] = useState("cursor");
    const [isFirstLoad, setIsFirstLoad] = useState(true);
    const [blocks, setBlocks] = useState([]);
    const workingField = useRef(null);
    const [currentBlock, setCurrentBlock] = useState(null);
    const cursorAction = useRef(null);
    // const [isShiftPressed, setIsShiftPressed] = useState(false);
    const [isCtrlPressed, setIsCtrlPressed] = useState(false);
    const [images, setImages] = useState([]);
    const [currentImage, setCurrentImage] = useState(null);



    const [imageForEffect, setImageForEffect] = useState(null)
    const [pixelSize, setPixelSize] = useState(10);
    const [refresh, setRefresh] = useState(false);


    const [format, setFormat] = useState('png');
    const [imagesForCollage, setImagesForCollage] = useState([])
    const [blockForCollage, setBlockForCollage] = useState(null)

    const [selectionMode, setSelectionMode] = useState("none");
    const [cropDirection, setCropDirection] = useState("left")
    const [cropValue, setCropValue] = useState(10)

    const [scaleFactor, setScaleFactor] = useState(1.1)
    const [stepFactor, setStepFactor] = useState(5)
    const [stepDirection ,setStepDirection] = useState("left")

    useEffect(() => {
        async function initializeUser() {
            try {
                const userData = await userModule.fetchUserInfo();
                setUser(userData);
                console.log(projectId)
            } catch (error) {
                console.error("Error fetching user:", error);
            }
        }
        initializeUser();
    }, []);



    useEffect(() => {
        loadProject();
    }, []);

    const loadProject = async () => {
        try {
            const response = await axios.get(`/api/projects/${projectId}`);
            const project = response.data;

            setBlocks((prevBlocks) => {

                const blocksById = {};

                project.collages.forEach((newBlock) => {
                    const blockId = newBlock.personalId || newBlock.id || Date.now();
                    if (!blocksById[blockId]) {
                        blocksById[blockId] = {
                            ...newBlock,
                            id: blockId,
                        };
                    }
                });

                return Object.values(blocksById);
            });



            setImages(
                project.images.map((image) => {
                    const validBase64 = image.base64.startsWith("data:")
                        ? image.base64
                        : `data:${image.type};base64,${image.picByte}`;
                    return {
                        ...image,
                        id: image.personalId || Date.now(),
                        base64: validBase64,
                    };
                })
            );




            setProject(project);
        } catch (error) {
            console.error("Error loading project:", error);
        }
    };


    useEffect(()=>{
        console.log(images)
        console.log(blocks)
    }, [images, blocks])


    const saveProject = async () => {
        const collagesToSave = blocks.map(({ id, ...rest }) => ({
            ...rest,
            id,
        }));

        try {
            await axios.put(`/api/projects/${projectId}/update`, {
                collages: collagesToSave,
            });

        } catch (error) {
            console.error("Error saving project:", error);
        }
    };



    const saveProjectImages = async () => {
        const imagesForBackEnd = images.map(image =>{
            const block = blocks.find(block =>
                image.left >= block.left &&
                image.left <= block.left + block.width &&
                image.top >= block.top &&
                image.top <= block.top + block.height
            );
            if(block){return {...image, blockId:block.id}}
            else {return {...image, blockId:null}}
        })
        const imagesToSave = imagesForBackEnd.map(({ id, blockId, base64, ...rest }) => ({
            ...rest,
            blockId,
            id,
            base64,
        }));



        try {
            await axios.put(`/api/projects/${projectId}/update/images`, {
                images: imagesToSave,
            });

        } catch (error) {
            console.error("Error saving project:", error);
        }
    };



    function exit(){
        navigate("/");
    }


    const handleLogout = () => {
        userModule.logout(navigate);
        onLogout();
    };


    useEffect(() => {
        if (workingField.current && !isFirstLoad) {
            if (currentAction === "block") {
                workingField.current.style.cursor = "crosshair";
            } else {
                workingField.current.style.cursor = "auto";
            }
        }
        setIsFirstLoad(false);
    }, [currentAction]);

    useEffect(() => {
        const handleKeyDown = (event) => {
            if (event.key === "Shift") {
            }
            if (event.key === "Control") {
                setIsCtrlPressed(true);
            }
        };

        const handleKeyUp = (event) => {
            if (event.key === "Shift") {
            }
            if (event.key === "Control") {
                setIsCtrlPressed(false);
            }
        };

        window.addEventListener("keydown", handleKeyDown);
        window.addEventListener("keyup", handleKeyUp);

        return () => {
            window.removeEventListener("keydown", handleKeyDown);
            window.removeEventListener("keyup", handleKeyUp);
        };
    }, []);







    useEffect(() => {
        if (!isFirstLoad) {
            if (workingField.current) {
                if (currentAction === "block") {
                    workingField.current.style.cursor = "crosshair";
                } else if (currentAction) {
                    workingField.current.style.cursor = "auto";
                }
            }
        }
        setIsFirstLoad(false);
    }, [currentAction]);




    const handleImageClick = (e, image) => {
        if (e.shiftKey) {
            e.stopPropagation();
            setCurrentImage(image);
            setCurrentBlock(null);
        }
    };



    const handleImageUpload = (event) => {
        const file = event.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = (e) => {
            const base64 = e.target.result;

            const img = new Image();
            img.onload = () => {
                const { naturalWidth, naturalHeight } = img;

                const clickHandler = (clickEvent) => {
                    const rect = workingField.current.getBoundingClientRect();
                    const offsetX = clickEvent.clientX - rect.left;
                    const offsetY = clickEvent.clientY - rect.top;

                    const block = blocks.find(block =>
                        clickEvent.clientX >= block.left &&
                        clickEvent.clientX <= block.left + block.width &&
                        clickEvent.clientY >= block.top &&
                        clickEvent.clientY <= block.top + block.height
                    );
                    if(block){
                        const blockWidth = block.width
                        const blockHeight = block.height
                        const koef = naturalHeight/naturalWidth
                        const newImage =  {
                            id: Math.floor(Date.now()),
                            name: file.name,
                            type: file.type,
                            base64,
                            top: offsetY,
                            left: offsetX,
                            width: Math.floor(blockWidth * 0.2),
                            height: Math.floor(blockWidth * 0.2 * koef),
                            naturalWidth: naturalWidth,
                            naturalHeight: naturalHeight,
                            blockId: block.id
                        };
                        console.log(naturalWidth)
                         setImages((prev) => [...prev, newImage]);

                    } else {
                        const newImage =  {
                            id: Math.floor(Date.now()),
                            name: file.name,
                            type: file.type,
                            base64,
                            top: offsetY,
                            left: offsetX,
                            width: naturalWidth,
                            height: naturalHeight,
                            naturalWidth: naturalWidth,
                            naturalHeight: naturalHeight,
                            blockId: null,
                        };
                        setImages((prev) => [...prev, newImage]);
                    }



                    workingField.current.removeEventListener("click", clickHandler);
                    event.target.value = "";

                };

                workingField.current.addEventListener("click", clickHandler);
            };

            img.src = base64;

        };

        reader.readAsDataURL(file);
    };

    useEffect(()=>{
        console.log(images)
    }, [images])

    useEffect(()=>{
        console.log(blocks)
    }, [blocks])

    useEffect(() => {
        if (currentBlock) {
            const block = document.querySelector(`.collage-${CSS.escape(currentBlock.id)}`);

            if (block) {
                block.style.border = "2px solid dodgerblue";
            }

            function handleClickOutside(e) {
                if (!e.target.closest('.control-panel') &&
                    (e.clientX < block.getBoundingClientRect().x ||
                        e.clientX > block.getBoundingClientRect().x + block.getBoundingClientRect().width ||
                        e.clientY < block.getBoundingClientRect().y ||
                        e.clientY > block.getBoundingClientRect().y + block.getBoundingClientRect().height)) {
                    block.style.border = "none";
                    setCurrentBlock(null);
                    document.removeEventListener("mousedown", handleClickOutside);
                }
            }

            document.addEventListener("mousedown", handleClickOutside);

            return () => {
                if (block) {
                    block.style.border = "none";
                }
                document.removeEventListener("mousedown", handleClickOutside);
            };
        }
    }, [currentBlock]);


    const addBlockCollage = (event) => {
        const startY = event.clientY;
        const startX = event.clientX;

        const newBlock = {
            id: Math.floor(Date.now()),
            top: startY,
            left: startX,
            width: 0,
            height: 0,
            backgroundColor: "#FFFFFF",
            padding: 5,
        };
        setBlocks((prevBlocks) => [...prevBlocks, newBlock]);
        setCurrentBlock(newBlock);

        const resizeBlock = (e) => {
            const xDirectionRightToLeft = e.clientX < startX;
            const yDirectionBottomToTop = e.clientY < startY;

            const updatedWidth = Math.abs(e.clientX - startX);
            const updatedHeight = Math.abs(e.clientY - startY);

            setBlocks((prevBlocks) =>
                prevBlocks.map((block) =>
                    block.id === newBlock.id
                        ? {
                            ...block,
                            top: yDirectionBottomToTop ? startY - updatedHeight : startY,
                            left: xDirectionRightToLeft ? startX - updatedWidth : startX,
                            width: updatedWidth,
                            height: updatedHeight,
                        }
                        : block
                )
            );

            setCurrentBlock((prevCurrentBlock) => ({
                ...prevCurrentBlock,
                top: yDirectionBottomToTop ? startY - updatedHeight : startY,
                left: xDirectionRightToLeft ? startX - updatedWidth : startX,
                width: updatedWidth,
                height: updatedHeight,
            }));

        };

        const finalizeBlock = () => {
            window.removeEventListener("mousemove", resizeBlock);
            window.removeEventListener("mouseup", finalizeBlock);
            setCurrentAction("cursor");
            cursorAction.current.click();
        };

        window.addEventListener("mousemove", resizeBlock);
        window.addEventListener("mouseup", finalizeBlock);
    };



    function chooseNewActionBtnDesign(event) {
        const panelItems = document.querySelectorAll(".panel__item");
        panelItems.forEach(item => {
            item.style.backgroundColor = "white";
        });
        event.currentTarget.style.backgroundColor = "rgb(237, 240, 247)";
    }


    const updateBlockProperty = (property, value) => {
        setBlocks((prevBlocks) =>
            prevBlocks.map((block) =>
                block.id === currentBlock.id ? { ...block, [property]: value } : block
            )
        );
        setCurrentBlock((prevCurrentBlock) => ({
            ...prevCurrentBlock,
            [property]: value,
        }));
    };



    const handleDragStart = (e, block) => {
        if (!isCtrlPressed) return;
        e.stopPropagation();
        setCurrentBlock(block);

        const offsetX = e.clientX - block.left;
        const offsetY = e.clientY - block.top;

        const handleDragMove = (e) => {
            setBlocks((prevBlocks) =>
                prevBlocks.map((b) =>
                    b.id === block.id
                        ? {
                            ...b,
                            left: e.clientX - offsetX,
                            top: e.clientY - offsetY,
                        }
                        : b
                )
            );
            setCurrentBlock((prevCurrentBlock) => ({
                ...prevCurrentBlock,
                left: e.clientX - offsetX,
                top: e.clientY - offsetY,
            }));
        };

        const handleDragEnd = () => {
            document.removeEventListener('mousemove', handleDragMove);
            document.removeEventListener('mouseup', handleDragEnd);
        };

        document.addEventListener('mousemove', handleDragMove);
        document.addEventListener('mouseup', handleDragEnd);
    };



    const handleImageDragStart = (e) => {
        if (!currentImage || !isCtrlPressed) return;
        e.stopPropagation();

        const offsetX = e.clientX - currentImage.left;
        const offsetY = e.clientY - currentImage.top;

        const handleDragMove = (e) => {
            setImages((prevImages) =>
                prevImages.map((img) =>
                    img.id === currentImage.id
                        ? {
                            ...img,
                            left: e.clientX - offsetX,
                            top: e.clientY - offsetY,
                        }
                        : img
                )
            );
            setCurrentImage((prevImage) => ({
                ...prevImage,
                left: e.clientX - offsetX,
                top: e.clientY - offsetY,
            }));
        };

        const handleDragEnd = () => {
            document.removeEventListener("mousemove", handleDragMove);
            document.removeEventListener("mouseup", handleDragEnd);
        };

        document.addEventListener("mousemove", handleDragMove);
        document.addEventListener("mouseup", handleDragEnd);
    };




    const resizeElements = (block) => {
        if (currentBlock && block.id === currentBlock.id) {
            return (
                <>
                    <div
                        className="current-block__resize-el current-block__resize-el-top-left"
                        style={{ position: 'absolute', zIndex: 10, width: '10px', height: '10px', backgroundColor: 'blue' }}
                        onMouseDown={(e) => startResizing(e, 'top-left')}
                    ></div>
                    <div
                        className="current-block__resize-el current-block__resize-el-top-right"
                        style={{ position: 'absolute', zIndex: 10, width: '10px', height: '10px', backgroundColor: 'blue' }}
                        onMouseDown={(e) => startResizing(e, 'top-right')}
                    ></div>
                    <div
                        className="current-block__resize-el current-block__resize-el-bottom-left"
                        style={{ position: 'absolute', zIndex: 10, width: '10px', height: '10px', backgroundColor: 'blue' }}
                        onMouseDown={(e) => startResizing(e, 'bottom-left')}
                    ></div>
                    <div
                        className="current-block__resize-el current-block__resize-el-bottom-right"
                        style={{ position: 'absolute', zIndex: 10, width: '10px', height: '10px', backgroundColor: 'blue' }}
                        onMouseDown={(e) => startResizing(e, 'bottom-right')}
                    ></div>
                    <div className="current-block__size-block">
                        W: {currentBlock.width}px H: {currentBlock.height}px
                    </div>
                </>
            );
        }
    };


    const startResizing = (e, direction) => {
        e.stopPropagation();
        e.preventDefault();

        const startX = e.clientX;
        const startY = e.clientY;
        const initialWidth = currentBlock.width;
        const initialHeight = currentBlock.height;
        const initialTop = currentBlock.top;
        const initialLeft = currentBlock.left;

        const resize = (e) => {
            let newWidth = initialWidth;
            let newHeight = initialHeight;
            let newTop = initialTop;
            let newLeft = initialLeft;


            if (direction.includes('right')) {
                newWidth = initialWidth + (e.clientX - startX);
            } else if (direction.includes('left')) {
                newWidth = initialWidth - (e.clientX - startX);
                newLeft = initialLeft + (e.clientX - startX);
            }

            if (direction.includes('bottom')) {
                newHeight = initialHeight + (e.clientY - startY);
            } else if (direction.includes('top')) {
                newHeight = initialHeight - (e.clientY - startY);
                newTop = initialTop + (e.clientY - startY);
            }

            setBlocks((prevBlocks) =>
                prevBlocks.map((block) =>
                    block.id === currentBlock.id
                        ? {
                            ...block,
                            width: newWidth > 0 ? newWidth : 0,
                            height: newHeight > 0 ? newHeight : 0,
                            top: newTop,
                            left: newLeft
                        }
                        : block
                )
            );

            setCurrentBlock((prevBlock) => ({
                ...prevBlock,
                width: newWidth > 0 ? newWidth : 0,
                height: newHeight > 0 ? newHeight : 0,
                top: newTop,
                left: newLeft
            }));
        };

        const stopResizing = () => {
            document.removeEventListener('mousemove', resize);
            document.removeEventListener('mouseup', stopResizing);
        };

        document.addEventListener('mousemove', resize);
        document.addEventListener('mouseup', stopResizing);
    };



    const startImageResizing = (e, direction) => {
        e.stopPropagation();
        e.preventDefault();

        const startX = e.clientX;
        const startY = e.clientY;
        const initialWidth = currentImage.width;
        const initialHeight = currentImage.height;
        const initialTop = currentImage.top;
        const initialLeft = currentImage.left;

        const resize = (e) => {
            let newWidth = initialWidth;
            let newHeight = initialHeight;
            let newTop = initialTop;
            let newLeft = initialLeft;

            if (direction.includes("right")) {
                newWidth = initialWidth + (e.clientX - startX);
            } else if (direction.includes("left")) {
                newWidth = initialWidth - (e.clientX - startX);
                newLeft = initialLeft + (e.clientX - startX);
            }

            if (direction.includes("bottom")) {
                newHeight = initialHeight + (e.clientY - startY);
            } else if (direction.includes("top")) {
                newHeight = initialHeight - (e.clientY - startY);
                newTop = initialTop + (e.clientY - startY);
            }

            setImages((prevImages) =>
                prevImages.map((img) =>
                    img.id === currentImage.id
                        ? {
                            ...img,
                            width: newWidth > 0 ? newWidth : 0,
                            height: newHeight > 0 ? newHeight : 0,
                            top: newTop,
                            left: newLeft,
                        }
                        : img
                )
            );

            setCurrentImage((prevImage) => ({
                ...prevImage,
                width: newWidth > 0 ? newWidth : 0,
                height: newHeight > 0 ? newHeight : 0,
                top: newTop,
                left: newLeft,
            }));
        };

        const stopResizing = () => {
            document.removeEventListener("mousemove", resize);
            document.removeEventListener("mouseup", stopResizing);
        };

        document.addEventListener("mousemove", resize);
        document.addEventListener("mouseup", stopResizing);
    };


    const handleBlockClick = (clickEvent) => {
        const rect = workingField.current.getBoundingClientRect();
        const offsetX = clickEvent.clientX - rect.left;
        const offsetY = clickEvent.clientY - rect.top;

        // Находим блок, по которому был произведен клик
        const clickedBlock = blocks.find(block =>
            offsetX >= block.left &&
            offsetX <= block.left + block.width &&
            offsetY >= block.top &&
            offsetY <= block.top + block.height
        );

        if (!clickedBlock) {
            console.log("Click outside block");
            return;
        }

        const imagesInsideBlock = images.filter(image =>
            image.left >= clickedBlock.left &&
            image.left <= clickedBlock.left + clickedBlock.width &&
            image.top >= clickedBlock.top &&
            image.top <= clickedBlock.top + clickedBlock.height
        );

        setBlockForCollage(() => clickedBlock)
        setImagesForCollage((prev) => [...prev, ...imagesInsideBlock]);

        console.log(`Added images for block: ${clickedBlock.id}`, imagesInsideBlock, blockForCollage);
    };



    const selectImage = (image) => {
        setImageForEffect(image);
    };

    const applyEffect = async (effect, params) => {
        if (!imageForEffect || !imageForEffect.id) return;

        try {
            console.log("Applying effect:", effect, "for image ID:", Math.trunc(imageForEffect.id));
            const response = await axios.post("/api/images/apply", {
                id: Math.trunc(imageForEffect.id),
                base64: imageForEffect.base64,
                type: imageForEffect.type,
            }, { params: { effect, ...params } });

            const updatedImage = response.data;

            setImages((prevImages) =>
                prevImages.map((img) =>
                    img.id === imageForEffect.id ? { ...img, base64: updatedImage } : img
                )
            );
        } catch (error) {
            console.error("Error applying effect:", error);
        }
    };

    const undoEffect = async () => {
        if (!imageForEffect || !imageForEffect.id) {
            console.error("Invalid imageForEffect or missing ID");
            return;
        }

        const cleanImageId = Math.trunc(imageForEffect.id);

        try {
            const response = await axios.post(`/api/images/undo?imageId=${cleanImageId}`);
            const restoredBase64 = response.data;

            setImages((prevImages) =>
                prevImages.map((img) =>
                    img.id === imageForEffect.id ? { ...img, base64: restoredBase64 } : img
                )
            );

            setImageForEffect((prev) => ({ ...prev, base64: restoredBase64 }));

            setRefresh((prev) => !prev);
        } catch (error) {
            console.error("Error undoing effect:", error);
        }
    };


    const downloadCollage = async (projectId, blockForCollage, imagesForCollage, fileType) => {
        try {
            const response = await axios.post(
                `/api/projects/${projectId}/generateCollage`,
                {
                    blockForCollage,
                    imagesForCollage,
                    fileType: format
                },
                {
                    responseType: "blob",
                }
            );

            const uniqueId = Math.floor(10000 + Math.random() * 90000);
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement("a");
            link.href = url;
            link.setAttribute("download", `collage_${uniqueId}.${format}`);
            document.body.appendChild(link);
            link.click();
            link.parentNode.removeChild(link);

            console.log("Collage downloaded successfully!");
        } catch (error) {
            console.error("Error downloading collage:", error);
        }
    };



    useEffect(() => {
        const saveData = async () => {
            try {
                await saveProject();
                setTimeout(()=>{
                    saveProjectImages();
                }, 1)
            } catch (error) {
                console.error("Error saving data:", error);
            }
        };

        saveData();
    }, [blocks, images]);





    const scaleElement = async (scaleFactor, selectionM) => {
        try {
            if (selectionM === "block" && currentBlock) {
                await axios.put(`/api/projects/${projectId}/scale`, null, {
                    params: { collageId: blockForCollage.id, scaleFactor, selectionMode: selectionM },
                });
                loadProject();
            } else if (selectionM === "image" && imageForEffect) {
                await axios.put(`/api/projects/${projectId}/scale`, null, {
                    params: { imageId: imageForEffect.id, scaleFactor, selectionMode: selectionM },
                });
                loadProject();
            } else {
                console.warn('No element selected for scaling');
            }
        } catch (error) {
            console.error('Error scaling element:', error);
        }
    };





    const moveElement = async (step, action, selectionM) => {
        try {
            if (selectionM === "block" && currentBlock) {
                await axios.put(`/api/projects/${projectId}/move`, null, {
                    params: { collageId: blockForCollage.id, step, selectionMode: selectionM, action },
                });
                loadProject();
            } else if (selectionM === "image" && imageForEffect) {
                await axios.put(`/api/projects/${projectId}/move`, null, {
                    params: { imageId: imageForEffect.id, step, selectionMode: selectionM, action },
                });
                loadProject();
            } else {
                console.warn('No element selected for scaling');
            }
        } catch (error) {
            console.error('Error scaling element:', error);
        }
    };




    const handleDeleteImage = async () => {
        try {
            await axios.put(`/api/projects/${projectId}/delete/image`, null, {
                params: { imageId: imageForEffect.id},
            });
            loadProject();
        } catch (error) {
            console.error("Error deleting image:", error);
        }
    };

    const handleDeleteCollage = async () => {
        try {
            await axios.put(`/api/projects/${projectId}/delete/collage`, null, {
                params: { blockId: blockForCollage.id},
            });
            loadProject();
        } catch (error) {
            console.error("Error deleting collage:", error);
        }
    };




    const handleDeleteProject = async () => {
        if (window.confirm("Are you sure you want to delete this project? This action cannot be undone.")) {
            try {
                await axios.delete(`/api/projects/${projectId}/delete`);
                navigate("/");
            } catch (error) {
                console.error("Error deleting project:", error);
            }
        }
    };


    const downloadImage = () => {
        if (!imageForEffect) {
            alert("Please select an image to download.");
            return;
        }

        const canvas = document.createElement("canvas");
        const ctx = canvas.getContext("2d");

        const img = new Image();
        img.onload = () => {
            canvas.width = img.width;
            canvas.height = img.height;

            ctx.drawImage(img, 0, 0);

            const imageURL = canvas.toDataURL(`image/${format}`);
            const link = document.createElement("a");
            link.href = imageURL;
            link.download = `image.${format}`;
            link.click();
        };

        img.onerror = (error) => {
            console.error("Error loading image for download:", error);
            alert("Failed to download the image.");
        };

        img.src = imageForEffect.base64;
    };




    return (
        <div>
            <div className="container">
                <header className="header">
                    {/*<button onClick={saveProject}>Save</button>*/}
                    <button onClick={handleDeleteProject}>Delete Project</button>
                    <button onClick={exit}>Exit</button>
                    <button  className="header__logout-btn">Logout</button>


                    <label className="upload-btn">
                        Upload Image
                        <input
                            type="file"
                            accept="image/*"
                            onChange={handleImageUpload}
                            style={{ display: "none" }}
                        />
                    </label>






                </header>

                <aside className="control-panel">

                    {imageForEffect && (
                        <div className="effect-panel" style={{ marginTop: "20px" }}>
                            <div className="effects">
                                <button onClick={() => applyEffect("grayscale")}>GrayScale</button>
                                <button onClick={() => applyEffect("sepia")}>Sepia</button>
                                <button onClick={() => applyEffect("blur")}>Blur</button>
                                <button onClick={() => applyEffect("invert-colors")}>Invert Colors</button>
                                <button onClick={() => applyEffect("threshold")}>Threshold</button>
                            </div>

                            <div className="pixelate">
                                <button onClick={() => applyEffect("pixelation", { pixelSize })}>Pixelate</button>
                                <label>
                                    Pixel Size:
                                    <input
                                        type="number"
                                        min="1"
                                        max="50"
                                        value={pixelSize}
                                        onChange={(e) => setPixelSize(parseInt(e.target.value, 10))}
                                    />
                                </label>
                            </div>


                            <div className="crop">
                                <button onClick={() => applyEffect("crop", {direction: cropDirection,
                                cropValue: cropValue})}>Crop</button>

                                <label>
                                    Direction:
                                    <select value={cropDirection} onChange={(e) => setCropDirection(e.target.value)}>
                                        <option value="left">left</option>
                                        <option value="top">top</option>
                                        <option value="bottom">bottom</option>
                                        <option value="right">right</option>
                                    </select>
                                </label>


                                <label>
                                    Step:
                                    <input
                                        type="number"
                                        min="1"
                                        max="50"
                                        value={cropValue}
                                        onChange={(e) => setCropValue(parseInt(e.target.value))}
                                    />
                                </label>
                            </div>

                            <button className="undo" onClick={undoEffect} style={{ border: "2px solid darkred" }}>
                                Undo Effect
                            </button>
                            <button className="delete_btn" onClick={handleDeleteImage}>Delete Image</button>


                            <div>
                                <label>
                                    Select Format:
                                    <select value={format} onChange={(e) => setFormat(e.target.value)}>
                                        <option value="jpg">JPG</option>
                                        <option value="png">PNG</option>
                                        <option value="tiff">TIFF</option>
                                        <option value="bmp">BMP</option>
                                        <option value="gif">GIF</option>
                                    </select>
                                </label>
                                <button onClick={downloadImage}>Download Image</button>
                            </div>



                            <button onClick={() => scaleElement(scaleFactor, "image")}>Increase Scale</button>
                            <button onClick={() => scaleElement(1/scaleFactor, "image")}>Decrease Scale</button>
                            <label>
                                Scale:
                                <input
                                    type="number"
                                    min="1"
                                    max="50"
                                    value={scaleFactor}
                                    onChange={(e) => setScaleFactor(parseFloat(e.target.value))}
                                />
                            </label>
                            <label>
                                Step:
                                <input
                                    type="number"
                                    min="1"
                                    max="50"
                                    value={stepFactor}
                                    onChange={(e) => setStepFactor(parseFloat(e.target.value))}
                                />
                            </label>

                            <label>
                                Direction:
                                <select value={stepDirection} onChange={(e) => setStepDirection(e.target.value)}>
                                    <option value="left">left</option>
                                    <option value="top">top</option>
                                    <option value="bottom">bottom</option>
                                    <option value="right">right</option>
                                </select>
                            </label>



                            <button onClick={() => moveElement(stepFactor, stepDirection, "image")}>Step</button>



                        </div>
                    )}

                    {currentBlock ? (
                        <div className="margin">
                            <p>Collage Actions</p>
                            <div className="control-panel__container">
                                <label className="control-panel__label">Width:</label>
                                <input
                                    type="number"
                                    className="control-panel__input"
                                    value={currentBlock.width}
                                    onChange={(e) => updateBlockProperty("width", Math.max(0, Number(e.target.value)))}
                                />
                            </div>

                            <div className="control-panel__container">
                                <label className="control-panel__label">Height:</label>
                                <input
                                    type="number"
                                    className="control-panel__input"
                                    value={currentBlock.height}
                                    onChange={(e) => updateBlockProperty("height", Math.max(0, Number(e.target.value)))}
                                />
                            </div>


                            <div className="control-panel__container">
                                <label className="control-panel__label">BackgroundColor:</label>
                                <input
                                    type="color"
                                    className="control-panel__input"
                                    value={currentBlock.backgroundColor}
                                    onChange={(e) => updateBlockProperty("backgroundColor", e.target.value)}
                                />
                            </div>
                            <div className="collage_download">
                                <label>
                                    Select Format:
                                    <select value={format} onChange={(e) => setFormat(e.target.value)}>
                                        <option value="jpg">JPG</option>
                                        <option value="png">PNG</option>
                                        <option value="tiff">TIFF</option>
                                        <option value="bmp">BMP</option>
                                        <option value="gif">GIF</option>
                                    </select>
                                </label>


                                <button onClick={()=>downloadCollage(projectId, blockForCollage, imagesForCollage)}>Generate Collage</button>
                            </div>

                            <button className="delete_btn" onClick={handleDeleteCollage}>Delete Collage</button>



                            <button onClick={() => scaleElement(scaleFactor, "block")}>Increase Scale</button>
                            <button onClick={() => scaleElement(1/scaleFactor, "block")}>Decrease Scale</button>
                            <label>
                                Scale:
                                <input
                                    type="number"
                                    min="1"
                                    max="50"
                                    value={scaleFactor}
                                    onChange={(e) => setScaleFactor(parseFloat(e.target.value))}
                                />
                            </label>
                            <label>
                                Step:
                                <input
                                    type="number"
                                    min="1"
                                    max="50"
                                    value={stepFactor}
                                    onChange={(e) => setStepFactor(parseFloat(e.target.value))}
                                />
                            </label>

                            <label>
                                Direction:
                                <select value={stepDirection} onChange={(e) => setStepDirection(e.target.value)}>
                                    <option value="left">left</option>
                                    <option value="top">top</option>
                                    <option value="bottom">bottom</option>
                                    <option value="right">right</option>
                                </select>
                            </label>



                            <button onClick={() => moveElement(stepFactor, stepDirection, "block")}>Step</button>

                        </div>
                    ) : (
                        <p>Select a collage or image to customize</p>
                    )}
                </aside>

                <div
                    ref={workingField}
                    className="working-field"
                    onMouseDown={currentAction === "block" ? addBlockCollage : undefined}
                >









                    {blocks.map((block) => (
                        <div
                            key={block.id}
                            className={`collage collage-${block.id}`}
                            style={{
                                position: 'absolute',
                                top: block.top,
                                left: block.left,
                                width: `${block.width}px`,
                                height: `${block.height}px`,
                                backgroundColor: block.backgroundColor || 'white',
                                border: currentBlock && currentBlock.id === block.id ? '2px solid dodgerblue' : 'none',
                                cursor: 'move',
                                padding: `${block.padding || 5}px`,
                                overflow: "hidden"
                            }}
                            onClick={handleBlockClick}
                            onMouseDown={(e) => handleDragStart(e, block)}
                        >


                            {resizeElements(block)}

                            {images
                                .filter(
                                    (image) =>
                                        image.left >= block.left &&
                                        image.left <= block.left + block.width &&
                                        image.top >= block.top &&
                                        image.top <= block.top + block.height
                                )
                                .map((image) => (
                                    <div
                                        key={image.id}
                                        style={{
                                            position: "absolute",
                                            top: `${image.top - block.top}px`,
                                            left: `${image.left - block.left}px`,
                                            width: image.width === "auto" ? image.width : image.width + "px",
                                            height: image.height,
                                            border: currentImage && currentImage.id === image.id ? "2px solid dodgerblue" : "none",
                                            zIndex: '1000000000'
                                        }}
                                        onClick={() => selectImage(image)}
                                        onMouseDown={(e) => handleImageDragStart(e, image)}
                                    >
                                        <img
                                            src={image.base64}
                                            alt={image.name}
                                            style={{
                                                width: "100%",
                                                height: "100%",
                                                zIndex: '1000000000'
                                            }}
                                            onClick={(e) => handleImageClick(e, image)}
                                        />
                                        {currentImage && currentImage.id === image.id && (
                                            <>
                                                <div
                                                    className="resize-handle top-left"
                                                    style={{
                                                        position: "absolute",
                                                        top: `-5px`,
                                                        left: `-5px`,
                                                        width: "10px",
                                                        height: "10px",
                                                        backgroundColor: "blue",
                                                        cursor: "nwse-resize",
                                                        zIndex: '1000000000'
                                                    }}
                                                    onMouseDown={(e) => startImageResizing(e, "top-left")}
                                                ></div>
                                                <div
                                                    className="resize-handle bottom-right"
                                                    style={{
                                                        position: "absolute",
                                                        bottom: `-5px`,
                                                        right: `-5px`,
                                                        width: "10px",
                                                        height: "10px",
                                                        backgroundColor: "blue",
                                                        cursor: "nwse-resize",
                                                        zIndex: '1000000000'
                                                    }}
                                                    onMouseDown={(e) => startImageResizing(e, "bottom-right")}
                                                ></div>
                                            </>
                                        )}
                                    </div>
                                ))}
                        </div>
                    ))}




                    {images
                        .filter(
                            (image) =>
                                !blocks.some(
                                    (block) =>
                                        image.left >= block.left &&
                                        image.left <= block.left + block.width &&
                                        image.top >= block.top &&
                                        image.top <= block.top + block.height
                                )
                        )
                        .map((image) => (
                            <div
                                key={image.id}
                                style={{
                                    position: "absolute",
                                    top: `${image.top}px`,
                                    left: `${image.left}px`,
                                    width: image.width * 0.2 + "px",
                                    height: image.height * 0.2 + "px",
                                    border: currentImage && currentImage.id === image.id ? "2px solid dodgerblue" : "none",
                                    zIndex: '1000000000'
                                }}
                                onClick={() => selectImage(image)}
                                onMouseDown={(e) => handleImageDragStart(e, image)}
                            >
                                <img
                                    src={image.base64}
                                    alt={image.name}
                                    style={{
                                        width: "100%",
                                        height: "100%",
                                        zIndex: '1000000000'
                                    }}
                                    onClick={(e) => handleImageClick(e, image)}
                                />
                                {currentImage && currentImage.id === image.id && (
                                    <>
                                        <div
                                            className="resize-handle top-left"
                                            style={{
                                                position: "absolute",
                                                top: `-5px`,
                                                left: `-5px`,
                                                width: "10px",
                                                height: "10px",
                                                backgroundColor: "blue",
                                                cursor: "nwse-resize",
                                                zIndex: '1000000000'
                                            }}
                                            onMouseDown={(e) => startImageResizing(e, "top-left")}
                                        ></div>
                                        <div
                                            className="resize-handle bottom-right"
                                            style={{
                                                position: "absolute",
                                                bottom: `-5px`,
                                                right: `-5px`,
                                                width: "10px",
                                                height: "10px",
                                                backgroundColor: "blue",
                                                cursor: "nwse-resize",
                                                zIndex: '1000000000'
                                            }}
                                            onMouseDown={(e) => startImageResizing(e, "bottom-right")}
                                        ></div>
                                    </>
                                )}
                            </div>
                        ))}





                </div>

                <div className="panel">
                    <div ref={cursorAction} className="panel__item item-cursor" onClick={(e) => {
                        chooseNewActionBtnDesign(e);
                        setCurrentAction("cursor");
                    }}>
                        <svg fill="none" height="24" viewBox="0 0 24 24" width="24" xmlns="http://www.w3.org/2000/svg"><path d="M3 3L10 22L12.0513 15.8461C12.6485 14.0544 14.0544 12.6485 15.846 12.0513L22 10L3 3Z" stroke="black" strokeLinejoin="round" strokeWidth="1.3"/></svg>
                    </div>
                    <div className="panel__item" onClick={(e) => {
                        chooseNewActionBtnDesign(e);
                        setCurrentAction("block");
                    }}>
                        <div></div>
                    </div>
                </div>
            </div>

        </div>
    );
}

