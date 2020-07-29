import React, { useState, useEffect } from 'react';
import * as Box from '3box'
import { images } from './TestImages'

const walletAddress = "replace with your wallet"
const spaces = ["dGallery"]

const storeAndFetch = async () => {
  const box = await Box.create()

  await box.auth(spaces, {
    address: walletAddress,
    provider: window.ethereum
  })
  await box.syncDone

  const space = await box.openSpace(spaces[0])

  await space.private.set('img-1', images[0])
  await space.private.set('img-2', images[1])

  const all = await space.private.all()

  return Object.values(all)
}

const App = () => {
  const [items, setItems] = useState([])

  useEffect(() => {
    storeAndFetch().then(result => {
      setItems(result)
    })
  }, [])

  return (
    <div>
      <div>Wallet: {walletAddress}</div>
      {items.map((item, index) => {
        return <img key={index} src={item} />
      })}

    </div>
  )
}

export default App;