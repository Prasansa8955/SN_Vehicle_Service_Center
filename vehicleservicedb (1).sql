-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 14, 2025 at 02:28 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `vehicleservicedb`
--

-- --------------------------------------------------------

--
-- Table structure for table `customer_vehicle`
--

CREATE TABLE `customer_vehicle` (
  `vehicle_id` varchar(10) NOT NULL,
  `customer_name` varchar(100) DEFAULT NULL,
  `nic` varchar(20) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `contact_number` varchar(15) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `vehicle_type` varchar(20) DEFAULT NULL,
  `vehicle_number` varchar(20) DEFAULT NULL,
  `vehicle_model` varchar(50) DEFAULT NULL,
  `service_type` varchar(20) DEFAULT NULL,
  `service_status` varchar(20) DEFAULT 'Pending',
  `service_date` date DEFAULT NULL,
  `cost` int(11) DEFAULT NULL,
  `bill_generated` tinyint(1) DEFAULT 0,
  `priority` varchar(10) NOT NULL DEFAULT 'Normal'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customer_vehicle`
--

INSERT INTO `customer_vehicle` (`vehicle_id`, `customer_name`, `nic`, `address`, `contact_number`, `date`, `vehicle_type`, `vehicle_number`, `vehicle_model`, `service_type`, `service_status`, `service_date`, `cost`, `bill_generated`, `priority`) VALUES
('VHC-0002', 'Hashi', '20028955', 'Kandy,7.', '0785496321', '2025-11-14', 'Car', 'GG-2548', 'BMW', 'Half', 'Completed', NULL, NULL, 0, 'VIP'),
('VHC-0003', 'Sandun', '2002895422', '6,Kandy', '0785421589', '2025-11-14', 'Car', 'KK-2589', 'BBB', 'Half', 'Completed', NULL, NULL, 0, 'Urgent'),
('VHC-0004', 'Danu', '258744555', '76,Kandy', '014785522', '2025-11-14', 'Car', 'DD-0987', 'MMKJ', 'Brake', 'Completed', NULL, NULL, 0, 'Normal'),
('VHC-0006', 'thtrh', 'htrh', 'tgth', '52752', '2025-11-14', 'Car', 'k9', 'ght', 'Half', 'Pending', NULL, NULL, 0, 'Urgent');

-- --------------------------------------------------------

--
-- Table structure for table `inventory`
--

CREATE TABLE `inventory` (
  `item_id` varchar(10) NOT NULL,
  `item_name` varchar(100) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `price` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `inventory`
--

INSERT INTO `inventory` (`item_id`, `item_name`, `quantity`, `price`) VALUES
('ITM-00d78', 'hfs', 25, 5000),
('ITM-e0f98', 'rfbrhdbf', 1212, 5255);

-- --------------------------------------------------------

--
-- Table structure for table `promotion`
--

CREATE TABLE `promotion` (
  `promo_id` varchar(20) NOT NULL,
  `promo_name` varchar(50) NOT NULL,
  `service_type` varchar(50) NOT NULL,
  `priority` enum('VIP','URGENT','NORMAL') NOT NULL,
  `promo_type` enum('Percentage','Fixed') NOT NULL,
  `promo_value` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `promotion`
--

INSERT INTO `promotion` (`promo_id`, `promo_name`, `service_type`, `priority`, `promo_type`, `promo_value`) VALUES
('PROMO-184bb', 'Sunday', 'OIL', 'NORMAL', 'Percentage', 20),
('PROMO-243d7', 'Sunday', 'Full', 'NORMAL', 'Percentage', 25),
('PROMO-47702', 'Sunday', 'Half', 'URGENT', 'Percentage', 12);

-- --------------------------------------------------------

--
-- Table structure for table `promotions`
--

CREATE TABLE `promotions` (
  `promo_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `service_inventory`
--

CREATE TABLE `service_inventory` (
  `service_id` varchar(10) NOT NULL,
  `item_id` varchar(10) NOT NULL,
  `quantity_used` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `customer_vehicle`
--
ALTER TABLE `customer_vehicle`
  ADD PRIMARY KEY (`vehicle_id`);

--
-- Indexes for table `inventory`
--
ALTER TABLE `inventory`
  ADD PRIMARY KEY (`item_id`);

--
-- Indexes for table `promotion`
--
ALTER TABLE `promotion`
  ADD PRIMARY KEY (`promo_id`);

--
-- Indexes for table `promotions`
--
ALTER TABLE `promotions`
  ADD PRIMARY KEY (`promo_id`);

--
-- Indexes for table `service_inventory`
--
ALTER TABLE `service_inventory`
  ADD PRIMARY KEY (`service_id`,`item_id`),
  ADD KEY `item_id` (`item_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `promotions`
--
ALTER TABLE `promotions`
  MODIFY `promo_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `service_inventory`
--
ALTER TABLE `service_inventory`
  ADD CONSTRAINT `service_inventory_ibfk_1` FOREIGN KEY (`service_id`) REFERENCES `customer_vehicle` (`vehicle_id`),
  ADD CONSTRAINT `service_inventory_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `inventory` (`item_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
