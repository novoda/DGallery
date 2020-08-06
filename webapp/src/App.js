import React, { useState, useEffect } from "react";
import * as Box from "3box";
import { images } from "./TestImages";

const walletAddress = "0x26B980c55354b353F395a34D4E6743105CC1D844";
//0x26B980c55354b353F395a34D4E6743105CC1D844
const spaces = ["dGallery"];

const storeAndFetch = async () => {
    const box = await Box.create();
    const accounts = await window.ethereum.enable();

    await box.auth(spaces, {
        address: walletAddress,
        provider: window.ethereum,
    });
    await box.syncDone;

    const space = await box.openSpace(spaces[0]);

    await space.private.set("img-1", images[0]);
    await space.private.set("img-2", images[1]);

    const all = await space.private.all();
    return Object.values(all);
};

const App = () => {
    const [items, setItems] = useState([]);
    useEffect(() => {
        storeAndFetch()
            .then((result) => {
                setItems(result);
            })
            .catch((e) => window.alert(e));
    }, []);

    return (
        <div>
            <div>Wallet: {walletAddress}</div>
            {items.map((item, index) => {
                return <img key={index} src={item} />;
            })}
        </div>
    );
};

export default App;
